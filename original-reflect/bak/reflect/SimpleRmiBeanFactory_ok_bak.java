package java.com.cheuks.bin.original.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cheuks.bin.original.annotation.RmiClient;
import com.cheuks.bin.original.annotation.RmiServer;
import com.cheuks.bin.original.common.util.Scan;
import com.cheuks.bin.original.common.util.ScanSimple;
import com.cheuks.bin.original.reflect.Reflection;
import com.cheuks.bin.original.reflect.test;
import com.cheuks.bin.original.reflect.rmi.RmiBeanFactory;
import com.cheuks.bin.original.reflect.rmi.SimpleRmiClient;
import com.cheuks.bin.original.reflect.rmi.handle.RmiClientConfigurationHandle;
import com.cheuks.bin.original.reflect.rmi.model.ClassBean;
import com.cheuks.bin.original.reflect.rmi.model.MethodBean;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public class SimpleRmiBeanFactory_ok_bak implements RmiBeanFactory {

	private final static Logger LOG = LoggerFactory.getLogger(SimpleRmiBeanFactory_ok_bak.class);

	private ClassPool pool = ClassPool.getDefault();

	private String suffixName = "$proxyClass";

	private Scan scan;

	private Reflection reflection = Reflection.newInstance();

	private static final Map<String, MethodBean> METHOD_BEAN = new ConcurrentHashMap<String, MethodBean>();
	private static final Map<String, ClassBean> BEAN = new ConcurrentHashMap<String, ClassBean>();
	private static final Map<String, ClassBean> CLASS_NAME_BEAN = new ConcurrentHashMap<String, ClassBean>();
	private static final Map<String, ClassBean> SIMPLE_CLASS_NAME_BEAN = new ConcurrentHashMap<String, ClassBean>();
	private static final List<ClassBean> CLASS_BEAN = new CopyOnWriteArrayList<ClassBean>();

	public void init(Map<String, Object> args) {
		try {
			String scanPath = (String) args.get("scan");
			scanRegistered(scanPath);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public <T> T getBean(Class<T> c) throws Throwable {
		return getBean(c.getName());
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(String serviceName) throws Throwable {
		ClassBean o = BEAN.get(serviceName);
		if (null == o)
			o = CLASS_NAME_BEAN.get(serviceName);
		if (null == o)
			o = SIMPLE_CLASS_NAME_BEAN.get(serviceName.toUpperCase());
		if (LOG.isDebugEnabled())
			LOG.debug("getBean:serviceName:" + serviceName + " result is null:" + (null == o));
		if (null != o) {
			synchronized (o) {
				if (o.isMultiInstance())
					return (T) o.getClass().newInstance();
				if (null == o.getInstance())
					return (T) o.setInstance(o.getClass().newInstance()).getInstance();
			}
			return (T) o.getInstance();
		}
		return null;
	}

	public MethodBean getMethod(String code) throws Throwable {
		return METHOD_BEAN.get(code);
	}

	public void scanRegistered(String path) throws Throwable {
		String[] paths = path.split(",");
		if (null == scan)
			scan = new ScanSimple();
		Map<String, Set<String>> classes = scan.doScan(path);
		// 过滤
		Class<?> tempClass;
		String version;
		boolean multiInstance;
		Set<String> classPaths;
		String className;
		Iterator<String> it;
		RmiClient client;
		RmiServer server;
		// MethodBean bean;
		Annotation tempAnnotation;
		String serviceName;
		Method[] methods;
		// 扫描CLASS
		for (String p : paths) {
			classPaths = classes.get(p);
			if (null != classPaths) {
				it = classPaths.iterator();
				while (it.hasNext()) {
					className = it.next();
					try {
						if (className.endsWith("class")) {
							tempClass = Class.forName(className.replace("/", ".").replace(".class", ""));
							// tempAnnotation = tempClass.getDeclaredAnnotation(RmiClient.class);
							if (null != (tempAnnotation = tempClass.getDeclaredAnnotation(RmiClient.class))) {
								// Rmi注解
								client = (RmiClient) tempAnnotation;

								version = client.version();
								multiInstance = client.multiInstance();
								// 注册名
								serviceName = client.serviceImplementation();
								// serviceName = serviceName.length() < 1 ? tempClass.getName() : serviceName;

								final ClassBean classBean = new ClassBean(tempClass, serviceName, version, multiInstance);

								CLASS_BEAN.add(classBean);
								System.out.println(tempClass.getName());
								// 生成代理类
								classBean.setProxyClassFile(classRefactor(classBean, com.cheuks.bin.original.reflect.rmi.RmiClient.class, SimpleRmiClient.class));
								if (!classBean.isMultiInstance())
									classBean.setInstance(classBean.getProxyClassFile().newInstance());

								BEAN.put(classBean.getRegistrationServiceName(), classBean);
								CLASS_NAME_BEAN.put(classBean.getOriginalClassFile().getName(), classBean);
								SIMPLE_CLASS_NAME_BEAN.put(classBean.getOriginalClassFile().getSimpleName().toUpperCase(), classBean);
							} else if (null != (tempAnnotation = tempClass.getDeclaredAnnotation(RmiServer.class))) {
								// Rmi注解
								server = (RmiServer) tempAnnotation;

								version = server.version();
								multiInstance = server.multiInstance();
								// 注册名
								serviceName = server.serviceName();
								// serviceName = serviceName.length() < 1 ? tempClass.getName() : serviceName;

								final ClassBean classBean = new ClassBean(tempClass, serviceName, version, multiInstance);
								classBean.setProxyClassFile(tempClass);
								// 分解Method
								//
								// 服务端
								methods = classBean.getOriginalClassFile().getDeclaredMethods();
								for (Method m : methods) {
									final MethodBean bean = MethodBean.builder(classBean, m);
									METHOD_BEAN.put(bean.getMd5Code(), bean);
								}
							}
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	// 普通类
	public Class<?> classRefactor(final ClassBean classBean, final Class<?> rmiClientInterface, final Class<?> rmiClientImpl) throws Throwable {

		boolean isInterface = classBean.getOriginalClassFile().isInterface();
		final String orginalClassName = classBean.getOriginalClassFile().getName();

		CtClass orginalClass = pool.get(orginalClassName);
		CtClass newClass = pool.makeClass(orginalClassName + getSuffixName());

		CtClass rmiClient = pool.get(rmiClientInterface.getName());
		CtClass rmiClientImp = pool.get(rmiClientImpl.getName());

		// 读取host,serverInterface,version

		Map<String, Object> xmlConfiguration = null;
		Map<String, Object> configuration = new RmiClientConfigurationHandle().getConfiguration(orginalClass, xmlConfiguration);
		// 继承/实现
		if (isInterface)
			newClass.addInterface(orginalClass);
		else
			newClass.setSuperclass(orginalClass);
		// 添加RmiClient接口
		// newClass.addInterface(rmiClient);

		// 实现 rmiclient里的field

		// 实现 rmiclient里的方法
		// CtMethod[] methods = rmiClientImp.getDeclaredMethods();
		// for (CtMethod m : methods) {
		// CtMethod tempMethod = CtNewMethod.copy(m, newClass, null);
		// newClass.addMethod(tempMethod);
		// }
		CtField rmiClientInvokeMethod = CtField.make(String.format("public %s rmiClientInvokeMethod=new %s();", rmiClientInterface.getName(), rmiClientImpl.getName()), newClass);
		newClass.addField(rmiClientInvokeMethod);
		// 方法重现
		CtMethod[] orginalClassMethods = orginalClass.getDeclaredMethods();
		// StringBuilder methodString = new StringBuilder();
		String methodString;
		for (final CtMethod m : orginalClassMethods) {
			// 转换模版: ((Integer) 1).intValue();
			methodString = generateMethod(m, null, convery4CodeByCtClass("rmiClientInvokeMethod.rmiInvoke(\"" + reflection.genericRmiMethodMd5Code(classBean.getRegistrationServiceName(), classBean.getVersion(), m) + "\",$args)", m.getReturnType()));
			newClass.addMethod(CtNewMethod.make(methodString, newClass));
		}

		newClass.writeFile("/Users/ben/Downloads/wwwwwwww/1.class");

		return newClass.toClass();
	}

	public String getSuffixName() {
		return suffixName;
	}

	public RmiBeanFactory setSuffixName(String suffixName) {
		this.suffixName = suffixName;
		return this;
	}

	public static void main(String[] args) throws Throwable {
		// RmiServiceFactory createProxyClass = new RmiServiceFactory();
		// createProxyClass.classRefactor(CacheFactory.class, RmiClient.class, SimpleRmiClient.class);
		// RmiClient rmiClient = (RmiClient) createProxyClass.tempClass.newInstance();
		// // System.out.println(rmiClient.rmiInvoke(null, null));
		// CacheFactory cf = (CacheFactory) createProxyClass.tempClass.newInstance();
		// System.out.println(cf.size());

		// RmiServiceFactory rsf = new RmiServiceFactory();
		// ClassPool pool = ClassPool.getDefault();
		// CtClass tempClass = pool.get(RmiClient.class.getName());
		// CtMethod[] methods = tempClass.getDeclaredMethods();
		// for (CtMethod m : methods)
		// System.out.println(rsf.generateMethodName(m));

		SimpleRmiBeanFactory_ok_bak rsf = new SimpleRmiBeanFactory_ok_bak();
		Map<String, Object> params = new HashMap<String, Object>();

		// params.put("scan", "com.cheuks.bin.*");
		// rsf.init(params);

		rsf.scanRegistered("com.cheuks.bin.*");

		ClassBean cb = CLASS_NAME_BEAN.get(test.class.getName());
		cb.setRegistrationServiceName("HHHHHH");

		cb = BEAN.get("ABCDEFG");

		cb.setRegistrationServiceName("CCCCC");

		for (Entry<String, ClassBean> en : BEAN.entrySet()) {
			System.out.println(en.getKey() + ":" + en.getValue().getRegistrationServiceName());
		}
		for (Entry<String, MethodBean> en : METHOD_BEAN.entrySet()) {
			System.out.println(en.getKey() + ":" + en.getValue().getCurrentMethod().getName());
		}

	}

	public String generateMethod(CtMethod m, String body, String returnBody) throws NotFoundException {
		StringBuilder sb = new StringBuilder();
		sb.append("public ").append(m.getReturnType().getName()).append(" ").append(m.getName()).append("(");
		CtClass[] params = m.getParameterTypes();
		if (null != params)
			for (int i = 0, len = params.length; i < len; i++) {
				sb.append(params[i].getName()).append(" p_" + i);
				if (i + 1 < len)
					sb.append(",");
			}
		sb.append(")");
		CtClass[] throwses = m.getExceptionTypes();
		if (null != throwses && throwses.length > 0) {
			sb.append("throws ");
			for (int i = 0, len = throwses.length; i < len; i++) {
				sb.append(throwses[i].getName());
				if (i + 1 < len)
					sb.append(",");
			}
		}
		sb.append("{");
		if (null != body)
			sb.append(body);
		if (!isVoid(m))
			if (null != returnBody)
				sb.append("return " + returnBody).append(";");
			else
				sb.append("return null;");
		else if (null != returnBody)
			sb.append(returnBody).append(";");
		sb.append("}");
		if (LOG.isDebugEnabled())
			LOG.debug(sb.toString());
		return sb.toString();
	}

	public boolean isVoid(CtMethod method) throws NotFoundException {
		return "void".equals(method.getReturnType().getSimpleName());
	}

	public final String convery4CodeByCtClass(String objectName, CtClass t) {
		if (null == t)
			return null;
		String typeName = t.getSimpleName();
		if (("void").equals(typeName))
			return objectName;
		if (("int").equals(typeName))
			return "((Integer)" + objectName + ").intValue()";
		else if (("boolean").equals(typeName)) {
			return "((Boolean" + objectName + ").booleanValue()";
		} else if (("float").equals(typeName)) {
			return "((Float" + objectName + ").floatValue()";
		}
		return String.format("(%s)%s", t.getName(), objectName);
	}
}
