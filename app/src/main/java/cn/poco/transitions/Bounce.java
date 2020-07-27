package cn.poco.transitions;

public class Bounce implements TweenBase
{
	@Override
	public float EaseIn(float t, float b, float c, float d)
	{
		return c - EaseOut(d - t, 0, c, d) + b;
	}

	@Override
	public float EaseOut(float t, float b, float c, float d)
	{
		if((t /= d) < (1f / 2.75f))
		{
			return c * (7.5625f * t * t) + b;
		}
		else if(t < (2f / 2.75f))
		{
			t -= (1.5f / 2.75f);
			return c * (7.5625f * t * t + .75f) + b;
		}
		else if(t < (2.5f / 2.75))
		{
			t -= (2.25f / 2.75f);
			return c * (7.5625f * t * t + .9375f) + b;
		}
		else
		{
			t -= (2.625f / 2.75f);
			return c * (7.5625f * t * t + .984375f) + b;
		}
	}

	@Override
	public float EaseInOut(float t, float b, float c, float d)
	{
		if(t < d * 0.5)
		{
			return EaseIn(t * 2, 0, c, d) * .5f + b;
		}
		else
		{
			return EaseOut(t * 2 - d, 0, c, d) * .5f + c * .5f + b;
		}
	}

}
