package cn.poco.transitions;

public class Sine implements TweenBase
{
	private static final double _HALF_PI = Math.PI * 0.5;

	@Override
	public float EaseIn(float t, float b, float c, float d)
	{
		return (float)(-c * Math.cos(t / d * _HALF_PI) + c + b);
	}

	@Override
	public float EaseOut(float t, float b, float c, float d)
	{
		return (float)(c * Math.sin(t / d * _HALF_PI) + b);
	}

	@Override
	public float EaseInOut(float t, float b, float c, float d)
	{
		return (float)(-c * 0.5 * (Math.cos(Math.PI * t / d) - 1) + b);
	}

}
