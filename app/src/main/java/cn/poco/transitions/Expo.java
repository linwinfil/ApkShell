package cn.poco.transitions;

public class Expo implements TweenBase
{
	@Override
	public float EaseIn(float t, float b, float c, float d)
	{
		return (t == 0) ? b : c * (float)Math.pow(2, 10 * (t / d - 1)) + b - c * 0.001f;
	}

	@Override
	public float EaseOut(float t, float b, float c, float d)
	{
		return (t == d) ? b + c : c * (-(float)Math.pow(2, -10 * t / d) + 1) + b;
	}

	@Override
	public float EaseInOut(float t, float b, float c, float d)
	{
		if(t == 0)
		{
			return b;
		}
		if(t == d)
		{
			return b + c;
		}
		if((t /= d * 0.5) < 1)
		{
			return c * 0.5f * (float)Math.pow(2, 10 * (t - 1)) + b;
		}
		
		return c * 0.5f * (-(float)Math.pow(2, -10 * --t) + 2) + b;
	}
}
