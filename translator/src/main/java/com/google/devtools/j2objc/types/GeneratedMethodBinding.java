/*
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.devtools.j2objc.types;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.devtools.j2objc.util.NameTable;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;

import java.util.List;

/**
 * Binding class for methods created during translation.
 *
 * @author Tom Ball
 */
public class GeneratedMethodBinding implements IMethodBinding {
  private final IMethodBinding delegate;
  private final String name;
  private final int modifiers;
  private final List<IBinding> parameters = Lists.newArrayList();
  private final ITypeBinding returnType;
  private final IMethodBinding methodDeclaration;
  private ITypeBinding declaringClass;
  private final boolean varargs;
  private final boolean isConstructor;
  private final boolean isSynthetic;

  public GeneratedMethodBinding(
      IMethodBinding delegate, String name, int modifiers, ITypeBinding returnType,
      IMethodBinding methodDeclaration, ITypeBinding declaringClass, boolean isConstructor,
      boolean varargs, boolean isSynthetic) {
    this.delegate = delegate;
    this.name = Preconditions.checkNotNull(name);
    this.modifiers = modifiers;
    this.returnType = returnType;
    this.methodDeclaration = methodDeclaration;
    this.declaringClass = declaringClass;
    this.isConstructor = isConstructor;
    this.varargs = varargs;
    this.isSynthetic = isSynthetic;
  }

  /**
   * Clone a method binding, so parameters can be added to it.
   */
  public GeneratedMethodBinding(IMethodBinding m) {
    this(null, m.getName(), m.getModifiers(), m.getReturnType(), null, m.getDeclaringClass(),
        m.isConstructor(), m.isVarargs(), m.isSynthetic());
    addParameters(m);
  }

  public static GeneratedMethodBinding newMethod(
      String name, int modifiers, ITypeBinding returnType, ITypeBinding declaringClass) {
    return new GeneratedMethodBinding(
        null, name, modifiers, returnType, null, declaringClass, false, false, true);
  }

  public static GeneratedMethodBinding newConstructor(ITypeBinding clazz, int modifiers) {
    return new GeneratedMethodBinding(
        null, NameTable.INIT_NAME, modifiers, Types.mapTypeName("void"), null, clazz, true, false,
        true);
  }

  public static GeneratedMethodBinding newOverridingMethod(
      IMethodBinding m, ITypeBinding declaringClass) {
    return new GeneratedMethodBinding(
        m, m.getName(), m.getModifiers(), m.getReturnType(), null, declaringClass,
        m.isConstructor(), m.isVarargs(), m.isSynthetic());
  }

  @Override
  public IAnnotationBinding[] getAnnotations() {
    return new IAnnotationBinding[0];
  }

  @Override
  public int getKind() {
    return IBinding.METHOD;
  }

  @Override
  public int getModifiers() {
    return modifiers;
  }

  @Override
  public boolean isDeprecated() {
    return false;
  }

  @Override
  public boolean isRecovered() {
    return false;
  }

  @Override
  public boolean isSynthetic() {
    return isSynthetic;
  }

  @Override
  public IJavaElement getJavaElement() {
    throw new AssertionError("not implemented");
  }

  @Override
  public String getKey() {
    throw new AssertionError("not implemented");
  }

  @Override
  public boolean isEqualTo(IBinding binding) {
    return equals(binding);
  }

  @Override
  public boolean isConstructor() {
    return isConstructor;
  }

  @Override
  public boolean isDefaultConstructor() {
    return isConstructor && parameters.isEmpty();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public ITypeBinding getDeclaringClass() {
    return declaringClass;
  }

  public void setDeclaringClass(ITypeBinding newClass) {
    declaringClass = newClass;
  }

  @Override
  public Object getDefaultValue() {
    return null;
  }

  @Override
  public IAnnotationBinding[] getParameterAnnotations(int paramIndex) {
    return new IAnnotationBinding[0];
  }

  @Override
  public ITypeBinding[] getParameterTypes() {
    List<ITypeBinding> types = Lists.newArrayList();
    for (IBinding param : parameters) {
      if (param instanceof IVariableBinding) {
        types.add(((IVariableBinding) param).getType());
      } else {
        types.add((ITypeBinding) param);
      }
    }
    return types.toArray(new ITypeBinding[types.size()]);
  }

  public void addParameter(IBinding param) {
    parameters.add(param);
  }

  public void addParameter(int index, IBinding param) {
    parameters.add(index, param);
  }

  public void addParameters(IMethodBinding method) {
    for (ITypeBinding paramType : method.getParameterTypes()) {
      parameters.add(new GeneratedVariableBinding(paramType, false, true, declaringClass, this));
    }
  }

  @Override
  public ITypeBinding getReturnType() {
    return returnType;
  }

  @Override
  public ITypeBinding[] getExceptionTypes() {
    // Obj-C doesn't have declared exceptions
    return new ITypeBinding[0];
  }

  @Override
  public ITypeBinding[] getTypeParameters() {
    throw new AssertionError("not implemented");
  }

  @Override
  public boolean isAnnotationMember() {
    return false;
  }

  @Override
  public boolean isGenericMethod() {
    return false;
  }

  @Override
  public boolean isParameterizedMethod() {
    return false;
  }

  @Override
  public ITypeBinding[] getTypeArguments() {
    return new ITypeBinding[0];
  }

  @Override
  public IMethodBinding getMethodDeclaration() {
    return methodDeclaration != null ? methodDeclaration : this;
  }

  @Override
  public boolean isRawMethod() {
    return false;
  }

  @Override
  public boolean isSubsignature(IMethodBinding otherMethod) {
    return delegate != null && delegate.isSubsignature(otherMethod);
  }

  @Override
  public boolean isVarargs() {
    return varargs;
  }

  @Override
  public boolean overrides(IMethodBinding method) {
    return delegate != null && (delegate.equals(method) || delegate.overrides(method));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((declaringClass == null) ? 0 : declaringClass.hashCode());
    result = prime * result + modifiers;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
    result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
    result = prime * result + (varargs ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof GeneratedMethodBinding)) {
      return false;
    }
    GeneratedMethodBinding other = (GeneratedMethodBinding) obj;
    return name.equals(other.name)
        && modifiers == other.modifiers
        && varargs == other.varargs
        // The returnType is null for constructors, so test equality first.
        && (returnType == null ? other.returnType == null : returnType.equals(other.returnType))
        && declaringClass.equals(other.declaringClass)
        && parameters.equals(other.parameters);
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    ASTNode.printModifiers(modifiers, sb);
    sb.append(returnType != null ? returnType.getName() : "<no type>");
    sb.append(' ');
    sb.append((name != null) ? name : "<no name>");
    sb.append('(');
    ITypeBinding[] params = getParameterTypes();
    for (int i = 0; i < params.length; i++) {
      sb.append(params[i].getName());
      if ((i + 1) < params.length) {
        sb.append(", ");
      }
    }
    sb.append(')');
    return sb.toString();
  }
}
