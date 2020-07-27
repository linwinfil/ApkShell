package cn.poco.transitions;

import android.os.Handler;
import android.os.Message;

public class SimpleTimer
{
	protected static final int MSG_TIMER = 0x1111;

	protected Thread m_thread;
	protected int m_delay;
	protected int m_repeatCount;
	protected SimpleTimer.TimerEventListener m_lst;
	protected Handler m_main;
	protected Runnable m_timerRun;
	protected boolean m_isCancel;

	public SimpleTimer(int delay, SimpleTimer.TimerEventListener listener)
	{
		m_delay = delay;
		m_lst = listener;
		m_repeatCount = -1;

		Init();
	}

	public SimpleTimer(int delay, int repeatCount, TimerEventListener listener)
	{
		m_delay = delay;
		m_repeatCount = repeatCount;
		m_lst = listener;

		Init();
	}

	protected void Init()
	{
		if(m_repeatCount >= 0)
		{
			m_timerRun = new Runnable()
			{
				@Override
				public void run()
				{
					for(int i = 1; i <= m_repeatCount; i++)
					{
						try
						{
							Thread.sleep(m_delay);
						}
						catch(Throwable e)
						{
							e.printStackTrace();
						}
						if(IsCancel())
						{
							return;
						}

						Message msg = m_main.obtainMessage();
						msg.arg1 = i;
						msg.what = MSG_TIMER;
						m_main.sendMessage(msg);
					}
				}
			};
		}
		else
		{
			m_timerRun = new Runnable()
			{
				@Override
				public void run()
				{
					int i = 0;
					while(true)
					{
						i++;
						try
						{
							Thread.sleep(m_delay);
						}
						catch(Throwable e)
						{
							e.printStackTrace();
						}
						if(IsCancel())
						{
							return;
						}

						Message msg = m_main.obtainMessage();
						msg.arg1 = i;
						msg.what = MSG_TIMER;
						m_main.sendMessage(msg);
					}
				}
			};
		}

		m_main = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch(msg.what)
				{
					case MSG_TIMER:
						if(!m_isCancel)
						{
							m_lst.OnTimer(msg.arg1);
						}
						break;

					default:
						break;
				}
			}

		};
	}

	public void Start()
	{
		if(m_thread == null)
		{
			m_thread = new Thread(m_timerRun);
			m_thread.start();
		}
	}

	public synchronized void Cancel()
	{
		m_isCancel = true;
	}

	public synchronized boolean IsCancel()
	{
		return m_isCancel;
	}

	public static interface TimerEventListener
	{
		public void OnTimer(int currentCount);
	}
}
