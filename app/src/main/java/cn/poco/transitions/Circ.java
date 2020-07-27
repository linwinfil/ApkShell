package cn.poco.transitions;

public class Circ implements TweenBase
{
	@Override
	public float EaseIn(float t, float b, float c, float d)
	{
		t /= d;
		return -c * ((float)Math.sqrt(1 - t * t) - 1) + b;
	}

	@Override
	public float EaseOut(float t, float b, float c, float d)
	{
		t = t / d - 1;
		return c * (float)Math.sqrt(1 - t * t) + b;
	}

	@Override
	public float EaseInOut(float t, float b, float c, float d)
	{
		if((t /= d * 0.5) < 1)
		{
			return -c * 0.5f * ((float)Math.sqrt(1 - t * t) - 1) + b;
		}
		else
		{
			t -= 2;
			return c * 0.5f * ((float)Math.sqrt(1 - t * t) + 1) + b;
		}
	}
}
