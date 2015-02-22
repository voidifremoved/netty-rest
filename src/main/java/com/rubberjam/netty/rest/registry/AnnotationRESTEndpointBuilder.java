package com.rubberjam.netty.rest.registry;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.core.annotation.AnnotationUtils;

import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;
import com.rubberjam.netty.rest.annotation.RESTMethod;
import com.rubberjam.netty.rest.protobuf.ProtobufException;

public class AnnotationRESTEndpointBuilder {

	
	private List<String> packages = new ArrayList<>();

	private RESTEndpointRegistry registry;
	
	private ServiceLookup lookup;
	
	public void build() throws Exception {
		
	    ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
	    FilterBuilder filterBuilder = new FilterBuilder();
	    List<URL> classpath = new ArrayList<>(packages.size());
	    for (String string : packages) {
	    	filterBuilder.include(FilterBuilder.prefix(string));
	    	classpath.addAll(ClasspathHelper.forPackage(string));
		}
	    configurationBuilder.setInputsFilter(filterBuilder);
		Reflections reflections = new Reflections(configurationBuilder
           .setUrls(classpath)
           .setScanners(new MethodAnnotationsScanner()));
		
		Set<Method> methods = reflections.getMethodsAnnotatedWith(RESTMethod.class); 
		
		for (Method method : methods) {
			RESTMethod annotation = AnnotationUtils.findAnnotation(method, RESTMethod.class);
			
			String svc = getServiceName(method);
			String methodName = getMethodName(method);
			Object target = lookup.lookup(method.getDeclaringClass(), svc);
			Class<? extends Message> messageType = (Class<? extends Message>)method.getParameterTypes()[1];
			final Method newBuilder = messageType.getMethod("newBuilder");
			
			
			registry.addEndpoint(svc, methodName, new RESTEndpoint(target, method, annotation.httpMethod().toNettyHttpMethod(), new MessageBuilder() {
				
				@Override
				public Builder newRequest() {
					try {
						return (Builder)newBuilder.invoke(null);
					} catch (Throwable t) {
						throw new ProtobufException("Cannot invoke builder method " + newBuilder, t);
					}
				}
			}));
		}
	}
	
	private String getServiceName(Method method) {
		Class<?> declaringClass = method.getDeclaringClass();
		return getServiceName(declaringClass);
	}

	public static String getServiceName(Class<?> declaringClass) {
		String simpleName = declaringClass.getSimpleName();
		if (simpleName.endsWith("Service")) {
			simpleName = simpleName.substring(0, simpleName.lastIndexOf("Service"));
		}
		simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
		return simpleName;
	}

	private String getMethodName(Method method) {
		return method.getName();
	}


	public void setRegistry(RESTEndpointRegistry registry) {
		this.registry = registry;
	}

	public void setPackages(List<String> packages) {
		this.packages = packages;
	}

	public void setLookup(ServiceLookup lookup) {
		this.lookup = lookup;
	}
	
	
	
}
