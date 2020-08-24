package cn.poco.transitions;

public class Back implements TweenBase
{
	public float s = 1.70158f;

	@Override
	public float EaseIn(float t, float b, float c, float d)
	{
		t /= d;
		return c * t * t * ((s + 1) * t - s) + b;
	}

	@Override
	public float EaseOut(float t, float b, float c, float d)
	{
		t = t / d - 1;
		return c * (t * t * ((s + 1) * t + s) + 1) + b;
	}

	@Override
	public float EaseInOut(float t, float b, float c, float d)
	{
		s *= 1.525f;
		if((t /= d * 0.5) < 1)
		{
			return c * 0.5f * (t * t * ((s + 1) * t - s)) + b;
		}
		else
		{
			t -= 2;
			return c / 2 * (t * t * ((s + 1) * t + s) + 2) + b;
		}
	}
}
