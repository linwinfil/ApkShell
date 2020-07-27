package cn.poco.transitions;

public class Elastic implements TweenBase
{
	private static final float _2PI = (float)Math.PI * 2;

	@Override
	public float EaseIn(float t, float b, float c, float d)
	{
		float a = 0;
		float p = 0;

		float s;
		if(t == 0)
		{
			return b;
		}
		if((t /= d) == 1)
		{
			return b + c;
		}
		if(p == 0)
		{
			p = d * .3f;
		}
		if(a == 0 || (c > 0 && a < c) || (c < 0 && a < -c))
		{
			a = c;
			s = p / 4;
		}
		else
		{
			s = p / _2PI * (float)Math.asin(c / a);
		}

		t -= 1;
		return -(a * (float)Math.pow(2, 10 * t) * (float)Math.sin((t * d - s) * _2PI / p)) + b;
	}

	@Override
	public float EaseOut(float t, float b, float c, float d)
	{
		float a = 0;
		float p = 0;

		float s;
		if(t == 0)
		{
			return b;
		}
		if((t /= d) == 1)
		{
			return b + c;
		}
		if(p == 0)
		{
			p = d * .3f;
		}
		if(a == 0 || (c > 0 && a < c) || (c < 0 && a < -c))
		{
			a = c;
			s = p / 4;
		}
		else
		{
			s = p / _2PI * (float)Math.asin(c / a);
		}
		
		return(a * (float)Math.pow(2, -10 * t) * (float)Math.sin((t * d - s) * _2PI / p) + c + b);
	}

	@Override
	public float EaseInOut(float t, float b, float c, float d)
	{
		float a = 0;
		float p = 0;

		float s;
		if(t == 0)
		{
			return b;
		}
		if((t /= d * 0.5f) == 2)
		{
			return b + c;
		}
		if(p == 0)
		{
			p = d * (.3f * 1.5f);
		}
		if(a == 0 || (c > 0 && a < c) || (c < 0 && a < -c))
		{
			a = c;
			s = p / 4;
		}
		else
		{
			s = p / _2PI * (float)Math.asin(c / a);
		}
		if(t < 1)
		{
			t -= 1;
			return -.5f * (a * (float)Math.pow(2, 10 * t) * (float)Math.sin((t * d - s) * _2PI / p)) + b;
		}
		
		t -= 1;
		return a * (float)Math.pow(2, -10 * t) * (float)Math.sin((t * d - s) * _2PI / p) * .5f + c + b;
	}

}
