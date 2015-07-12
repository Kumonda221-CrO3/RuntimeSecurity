package org.kucro3.rtsecurity;

@SuppressWarnings({ "restriction", "deprecation" })
public class RtUtil {
	public static final String getCaller(int count)
	{
		return dumpName(count);
//		return Thread.currentThread().getStackTrace()[count].getClassName();
	}
	
	public static final Class<?> getCallerClass(int count) throws ClassNotFoundException
	{
		return dumpClass(count);
//		return Class.forName(getCaller(count));
	}
	
	public static final boolean checkCallerBool(int count, String... name)
	{
		String caller = getCaller(count);
		for(int i = 0; i < name.length; i++)
			if(caller.equals(name[i]))
				return true;
		return false;
	}
	
	public static final boolean checkCallerBool(int count, Class<?>... clz)
	{
		try {
			Class<?> caller = getCallerClass(count);
			for(int i = 0; i < clz.length; i++)
				if(caller.equals(clz[i]))
					return true;
		} catch (ClassNotFoundException e) {
		}
		return false;
	}
	
	public static final void checkCaller(int count, String... name)
	{
		checkCaller0(count, new RtCallingDisallowedException(), name);
	}
	
	public static final void checkCaller(int count, RuntimeException e, String... name)
	{
		checkCaller0(count, e, name);
	}
	
	public static final void checkCaller(int count, Class<?>... clz)
	{
		checkCaller0(count, new RtCallingDisallowedException(), clz);
	}
	
	public static final void checkCaller(int count, RuntimeException e, Class<?>... clz)
	{
		checkCaller0(count, e, clz);
	}
	
	private static final void checkCaller0(int count, RuntimeException e, String... name)
	{
		if(!checkCallerBool(count + 3, name))
		{
			e.setStackTrace(fakeStackTrace(4));
			throw e;
		}
	}
	
	private static final void checkCaller0(int count, RuntimeException e, Class<?>... clz)
	{
		if(!checkCallerBool(count + 3, clz))
		{
			e.setStackTrace(fakeStackTrace(4));
			throw e;
		}
	}
	
	private static final Class<?> dumpClass(int i) throws ClassNotFoundException
	{
		if(fastDumpAvailable())
			return sun.reflect.Reflection.getCallerClass(i + 2);
		else
			return Class.forName(Thread.currentThread().getStackTrace()[i + 2].getClassName());
	}
	
	private static final String dumpName(int i)
	{
		if(fastDumpAvailable())
			return sun.reflect.Reflection.getCallerClass(i + 2).getName();
		else
			return Thread.currentThread().getStackTrace()[i + 2].getClassName();
	}
	
	static final StackTraceElement[] fakeStackTrace(int backcount)
	{
		assert backcount >= 0;
		
		if(backcount == 0)
			return Thread.currentThread().getStackTrace();
		
		StackTraceElement[] st = Thread.currentThread().getStackTrace();
		StackTraceElement[] newst = new StackTraceElement[st.length - backcount];
		System.arraycopy(st, backcount, newst, 0, newst.length);
		
		return newst;
	}
	
	public static boolean fastDumpAvailable()
	{
		return sunAvailable;
	}
	
	static {
		boolean flag = false;
		try {
			Class.forName("sun.reflect.Reflection");
			flag = sun.reflect.Reflection.getCallerClass(1).equals(RtUtil.class);
		} catch (ClassNotFoundException e) {
		}
		sunAvailable = flag;
	}
	
	private static final boolean sunAvailable;
}
