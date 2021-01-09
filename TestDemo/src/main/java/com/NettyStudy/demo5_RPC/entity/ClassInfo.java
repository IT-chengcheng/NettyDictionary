package com.NettyStudy.demo5_RPC.entity;

import java.io.Serializable;

public class ClassInfo implements Serializable{
    private String ClassName;
    private String methodName;
    private Object[] args;
    private Class[] clazzType;

    public ClassInfo() {
    }

    public ClassInfo(String className, String methodName, Object[] args, Class[] clazzType) {
        ClassName = className;
        this.methodName = methodName;
        this.args = args;
        this.clazzType = clazzType;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public void setClazzType(Class[] clazzType) {
        this.clazzType = clazzType;
    }

    public String getClassName() {
        return ClassName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public Class[] getClazzType() {
        return clazzType;
    }
}
