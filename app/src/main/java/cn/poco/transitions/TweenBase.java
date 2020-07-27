package cn.poco.transitions;

public interface TweenBase
{
	/**
	 * @param t 当前时间[0,d]
	 * @param b 偏移
	 * @param c 差值
	 * @param d 持续时间
	 * @return
	 */
	public float EaseIn(float t, float b, float c, float d);
	public float EaseOut(float t, float b, float c, float d);
	public float EaseInOut(float t, float b, float c, float d);
}
