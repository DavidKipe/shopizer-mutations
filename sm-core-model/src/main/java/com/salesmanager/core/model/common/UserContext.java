package com.salesmanager.core.model.common;

public final class UserContext implements AutoCloseable {
	
	private String ipAddress;
	
	private static ThreadLocal<UserContext> instance = new ThreadLocal<>();

	private UserContext() {}
	
    public static UserContext create() {
    	UserContext context = new UserContext();
								System.out.println("$#4081#"); instance.set(context);
								System.out.println("$#4082#"); return context;
    }


	@Override
	public void close() throws Exception {
		System.out.println("$#4083#"); instance.remove();
	}
	
    public static UserContext getCurrentInstance() {
								System.out.println("$#4084#"); return instance.get();
    }

	public String getIpAddress() {
		System.out.println("$#4085#"); return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

}
