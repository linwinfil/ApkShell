package cn.poco.transitions;

public class Linear implements TweenBase
{
	@Override
	public float EaseIn(float t, float b, float c, float d)
	{
		return c * t / d + b;
	}

	@Override
	public float EaseOut(float t, float b, float c, float d)
	{
		return EaseIn(t, b, c, d);
	}

	@Override
	public float EaseInOut(float t, float b, float c, float d)
	{
		return EaseIn(t, b, c, d);
	}
}
