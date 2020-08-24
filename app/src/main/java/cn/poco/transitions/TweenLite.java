package cn.poco.transitions;

public class TweenLite
{
	public static final int EASE_IN = 0x1;
	public static final int EASE_OUT = 0x2;
	public static final int EASE_IN_OUT = 0x4;

	public static final int EASING_BACK = 0x00000010;
	public static final int EASING_BOUNCE = 0x00000020;
	public static final int EASING_CIRC = 0x00000040;
	public static final int EASING_ELASTIC = 0x00000080;
	public static final int EASING_EXPO = 0x00000100;
	public static final int EASING_LINEAR = 0x00000200;
	public static final int EASING_QUINT = 0x00000400;
	public static final int EASING_QUART = 0x00000800;
	public static final int EASING_CUBIC = 0x00001000;
	public static final int EASING_QUAD = 0x00002000;
	public static final int EASING_STRONG = 0x00004000;
	public static final int EASING_SINE = 0x00008000;

	protected float m_start; //开始值
	protected float m_end; //结束值
	protected long m_delay; //启动延迟
	protected long m_duration; //持续时间
	protected long m_startTime; //启动时间
	protected int m_type; //动画类型
	protected TweenBase m_core; //算法核心

	public TweenLite()
	{
	}

	public TweenLite(float start, float end, long duration)
	{
		Init(start, end, duration);
	}

	public TweenLite(float start, float end, long delay, long duration)
	{
		Init(start, end, delay, duration);
	}

	public void Init(float start, float end, long duration)
	{
		Init(start, end, 0, duration);
	}

	public void Init(float start, float end, long delay, long duration)
	{
		m_start = start;
		m_end = end;
		m_delay = delay;
		m_duration = duration;
	}

	public float GetEnd()
	{
		return m_end;
	}

	public float GetStart()
	{
		return m_start;
	}

	//************mode 1 start*************
	protected boolean M1_IS_STANDARD = true;
	protected boolean M1_IS_FINISH = true;

	public void M1Start(int type)
	{
		m_type = type;
		m_core = GetCore(m_type);

		m_startTime = System.currentTimeMillis();
		M1_IS_FINISH = false;
		M1_IS_STANDARD = true;
	}

	//特别模式,只有匀减速
	protected float m_sv;
	protected float m_a;
	protected long m_endTime;

	/**
	 * 
	 * @param start
	 *            起始位置
	 * @param end
	 *            结束位置
	 * @param sv
	 *            初速度(px/s)
	 * @param a
	 *            减速度(px/s^2)
	 */
	public void M1Start(float start, float end, long delay, float sv, float a)
	{
		m_start = start;
		m_end = end;
		m_sv = sv;
		m_a = a;

		float dx = m_end - m_start;
		if(dx > 0)
		{
			m_sv = Math.abs(m_sv);
			m_a = -Math.abs(m_a);
		}
		else if(dx < 0)
		{
			m_sv = -Math.abs(m_sv);
			m_a = Math.abs(m_a);
		}
		else
		{
			m_sv = 0;
			m_a = 0;
		}

		m_startTime = System.currentTimeMillis();
		m_endTime = m_startTime + m_delay;
		if(a != 0)
		{
			m_endTime += (long)Math.abs(sv / a * 1000);
		}
		M1_IS_FINISH = false;
		M1_IS_STANDARD = false;
	}

	public float M1GetPos()
	{
		if(M1_IS_STANDARD)
		{
			if(m_core != null)
			{
				long t = System.currentTimeMillis() - (m_startTime + m_delay);
				if(t >= m_duration)
				{
					return M1End();
				}
				else if(t <= 0)
				{
					return m_start;
				}
				float c = m_end - m_start;
				if((m_type & EASE_IN) != 0)
				{
					return m_core.EaseIn(t, m_start, c, m_duration);
				}
				else if((m_type & EASE_OUT) != 0)
				{
					return m_core.EaseOut(t, m_start, c, m_duration);
				}
				else if((m_type & EASE_IN_OUT) != 0)
				{
					return m_core.EaseInOut(t, m_start, c, m_duration);
				}
			}

			return m_start;
		}
		else
		{
			long t = System.currentTimeMillis();
			if(t >= m_endTime)
			{
				return M1End();
			}
			else if(t <= m_startTime + m_delay)
			{
				return m_start;
			}
			t -= m_startTime + m_delay;
			float t2 = t / 1000f;
			float x = m_sv * t2 + 1f / 2f * m_a * t2 * t2;
			//System.out.println(x);
			if(Math.abs(x) >= Math.abs(m_end - m_start))
			{
				//System.out.println("-------------------");
				return M1End();
			}

			return m_start + x;
		}
	}

	public float M1End()
	{
		M1_IS_FINISH = true;
		return m_end;
	}

	public boolean M1IsFinish()
	{
		return M1_IS_FINISH;
	}

	//************mode 1 end***************

	//************mode 2 start*************
	public float[] M2GetPos(int type, int frames)
	{
		m_type = type;
		m_core = GetCore(m_type);
		if(m_core != null)
		{
			int count = (int)Math.round(m_duration / 1000f * frames);
			if(count < 1)
			{
				count = 1;
			}
			float[] out = new float[count];
			float piece = 1000f / frames;
			float c = m_end - m_start;
			float d = piece * count;
			for(int i = 1; i < count; i++)
			{
				if((m_type & EASE_IN) != 0)
				{
					out[i - 1] = m_core.EaseIn(i * piece, m_start, c, d);
				}
				else if((m_type & EASE_OUT) != 0)
				{
					out[i - 1] = m_core.EaseOut(i * piece, m_start, c, d);
				}
				else if((m_type & EASE_IN_OUT) != 0)
				{
					out[i - 1] = m_core.EaseInOut(i * piece, m_start, c, d);
				}
				else
				{
					out[i - 1] = m_end;
				}
			}
			out[count - 1] = m_end;

			return out;
		}

		return null;
	}

	//************mode 2 end***************

	//************mode 3 start*************
	protected SimpleTimer m_timer;

	public void M3DoAnimation(final int type, final int frames, final TweenLite.Callback cb)
	{
		final float[] pos = M2GetPos(type, frames);
		if(pos != null)
		{
			int delay = 1000 / frames;
			if(delay <= 0)
			{
				delay = 1;
			}
			M3Cancel();
			m_timer = new SimpleTimer(delay, pos.length, new SimpleTimer.TimerEventListener()
			{
				@Override
				public void OnTimer(int currentCount)
				{
					if(cb != null)
					{
						cb.OnMotionChange(pos[currentCount - 1], currentCount, pos.length);
					}
				}
			});
			m_timer.Start();
		}
	}

	public void M3Cancel()
	{
		if(m_timer != null)
		{
			m_timer.Cancel();
			m_timer = null;
		}
	}

	public static interface Callback
	{
		public void OnMotionChange(float position, int current, int total);
	}

	//************mode 3 end***************

	protected TweenBase GetCore(int type)
	{
		if((m_type & EASING_BACK) != 0)
		{
			return new Back();
		}
		else if((m_type & EASING_BOUNCE) != 0)
		{
			return new Bounce();
		}
		else if((m_type & EASING_CIRC) != 0)
		{
			return new Circ();
		}
		else if((m_type & EASING_ELASTIC) != 0)
		{
			return new Elastic();
		}
		else if((m_type & EASING_EXPO) != 0)
		{
			return new Expo();
		}
		else if((m_type & EASING_LINEAR) != 0)
		{
			return new Linear();
		}
		else if((m_type & EASING_QUINT) != 0)
		{
			return new Quint();
		}
		else if((m_type & EASING_QUART) != 0)
		{
			return new Quart();
		}
		else if((m_type & EASING_CUBIC) != 0)
		{
			return new Cubic();
		}
		else if((m_type & EASING_QUAD) != 0)
		{
			return new Quad();
		}
		else if((m_type & EASING_STRONG) != 0)
		{
			return new Strong();
		}
		else if((m_type & EASING_SINE) != 0)
		{
			return new Sine();
		}

		return null;
	}
}
