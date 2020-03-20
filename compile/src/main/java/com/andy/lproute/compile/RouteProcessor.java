package com.andy.lproute.compile;

import com.andy.lproute.annotation.Interceptor;
import com.andy.lproute.annotation.Route;
import com.andy.lproute.base.Constants;
import com.andy.lproute.bean.ComponentInfo;
import com.andy.lproute.bean.InterceptorInfo;
import com.andy.lproute.provider.IGroup;
import com.andy.lproute.provider.IInterceptorInfo;
import com.andy.lproute.util.RouteUtils;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * @ClassName: RouteProcessor
 * @Description: custom processor
 * @Author: andy
 * @Date: 2020/3/14 10:42
 */
@AutoService(Processor.class)
public class RouteProcessor extends AbstractProcessor {
    private static final int INIT_CAPACITY = 10;
    private Map<String, TypeSpec.Builder> mTypeMap = new HashMap(INIT_CAPACITY);
    private ProcessingEnvironment mProcessingEnvironment;
    private TypeSpec.Builder mInterceptorTypeSpecBuilder;

    public RouteProcessor() {
    }


    @Override
    public Set<String> getSupportedOptions() {
        println("getSupportedOptions");
        return super.getSupportedOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        println("getSupportedAnnotationTypes");
        Set<String> set = new HashSet(3);
        set.add(Route.class.getCanonicalName());
        set.add(Interceptor.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        println("getSupportedSourceVersion");
        return SourceVersion.RELEASE_8;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        mProcessingEnvironment = processingEnvironment;
        println("init");
        super.init(processingEnvironment);
    }

    @Override
    public Iterable<? extends Completion> getCompletions(Element element, AnnotationMirror annotationMirror, ExecutableElement executableElement, String s) {
        println("getCompletions");
        return super.getCompletions(element, annotationMirror, executableElement, s);
    }

    @Override
    protected synchronized boolean isInitialized() {
        println("isInitialized");
        return super.isInitialized();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        println("process----------");
        for (TypeElement element : set) {
            println("element:" + element);
            if (element.toString().equals(Route.class.getCanonicalName())) {
                doRoute(roundEnvironment);
            } else if (element.toString().equals(Interceptor.class.getCanonicalName())) {
                doInterceptor(roundEnvironment);
            }
        }
        return false;
    }

    private void doInterceptor(RoundEnvironment roundEnvironment) {
        Set<Element> elements = (Set<Element>) roundEnvironment.getElementsAnnotatedWith(Interceptor.class);
        for (Element element:elements) {
            if (!(element instanceof TypeElement)) {
                continue;
            }
            Interceptor interceptor = element.getAnnotation(Interceptor.class);
            createOrAppendInterceptor(interceptor, (TypeElement) element);
        }

        println(mInterceptorTypeSpecBuilder.toString());
        JavaFile javaFile = JavaFile.builder(Constants.PACKAGE_COMPILE, mInterceptorTypeSpecBuilder.build())
                .build();
        try {
            javaFile.writeTo(mProcessingEnvironment.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doRoute(RoundEnvironment roundEnvironment) {
        // 获取被Route注解的类型元素
        Set<Element> elements = (Set<Element>) roundEnvironment.getElementsAnnotatedWith(Route.class);
        for (Element element : elements) {
            println("round element:" + element);
            if (!(element instanceof TypeElement)) {
                continue;
            }
//            printTypeElement((TypeElement) element);
            Route route = element.getAnnotation(Route.class);
            if (route != null) {
                createOrAppendRouter(route, (TypeElement) element);
                println("createOrAppend");
                continue;
            }
        }
        println("write...");
        Collection<TypeSpec.Builder> typeSpecs = mTypeMap.values();
        for (TypeSpec.Builder typeSpec : typeSpecs) {
            println(typeSpec.toString());
            JavaFile javaFile = JavaFile.builder(Constants.PACKAGE_COMPILE, typeSpec.build())
                    .build();
            try {
                javaFile.writeTo(mProcessingEnvironment.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private TypeSpec.Builder createInterceptor(String className, Interceptor interceptor, TypeElement element) {
        ClassName superInterface = ClassName.get(RouteUtils.getPackageName(IInterceptorInfo.class),
                IInterceptorInfo.class.getSimpleName());
        ClassName hashMapClassName = ClassName.get(RouteUtils.getPackageName(Map.class),
                Map.class.getSimpleName());
        printClassName(hashMapClassName);
        ClassName stringClassName = ClassName.get(RouteUtils.getPackageName(String.class),
                String.class.getSimpleName()
        );
        printClassName(stringClassName);
        ClassName interceptorInfoClassName = ClassName.get(RouteUtils.getPackageName(InterceptorInfo.class),
                InterceptorInfo.class.getSimpleName()
        );
        printClassName(interceptorInfoClassName);
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(hashMapClassName,
                stringClassName,
                interceptorInfoClassName
        );
        ParameterSpec parameterSpec = ParameterSpec.builder(parameterizedTypeName, "map")
                .build();
        CodeBlock codeBlock = CodeBlock.builder()
                .addStatement("map.put($S, new $N($N.class, $S, $L));",
                        interceptor.name(),
                        interceptorInfoClassName.simpleName(),
                        element.toString(),
                        interceptor.name(),
                        interceptor.priority()
                )
                .build();
        MethodSpec loadInfoMethodSpec = MethodSpec.methodBuilder("loadInfo")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec)
                .addCode(codeBlock)
                .build();
        return TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(superInterface)
                .addMethod(loadInfoMethodSpec);
    }

    private TypeSpec.Builder createRouter(Route route, String className, TypeElement element) {
        ClassName superInterface = ClassName.get(RouteUtils.getPackageName(IGroup.class),
                IGroup.class.getSimpleName());
        printClassName(superInterface);
        ClassName hashMapClassName = ClassName.get(RouteUtils.getPackageName(Map.class),
                Map.class.getSimpleName());
        printClassName(hashMapClassName);
        ClassName stringClassName = ClassName.get(RouteUtils.getPackageName(String.class),
                String.class.getSimpleName()
        );
        printClassName(stringClassName);
        ClassName componentInfoClassName = ClassName.get(RouteUtils.getPackageName(ComponentInfo.class),
                ComponentInfo.class.getSimpleName()
        );
        printClassName(componentInfoClassName);
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(hashMapClassName,
                stringClassName,
                componentInfoClassName
        );
        ParameterSpec parameterSpec = ParameterSpec.builder(parameterizedTypeName, "map")
                .build();
        CodeBlock codeBlock = CodeBlock.builder()
                .addStatement("map.put($S, new $N($S, $N.class));",
                        route.path(),
                        componentInfoClassName.simpleName(),
                        route.path(),
                        element.toString()
                )
                .build();
        println(codeBlock.toString());
        MethodSpec loadInfoMethodSpec = MethodSpec.methodBuilder("loadInfo")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(parameterSpec)
                .addCode(codeBlock)
                .build();
        return TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(superInterface)
                .addMethod(loadInfoMethodSpec);
    }

    /**
     * create a java file or append content on it
     *
     * @param interceptor   interceptor
     * @param element type element
     */
    private TypeSpec.Builder createOrAppendInterceptor(Interceptor interceptor, TypeElement element) {

        if (mInterceptorTypeSpecBuilder == null) {
            mInterceptorTypeSpecBuilder = createInterceptor(Constants.CLASS_INTERCEPTOR, interceptor, element);
            println("interceptor create.");
        } else {
            println("interceptor exsit");
            CodeBlock codeBlock = CodeBlock.builder()
                    .addStatement("map.put($S, new $N($N.class, $S, $L));",
                            interceptor.name(),
                            InterceptorInfo.class.getSimpleName(),
                            element.toString(),
                            interceptor.name(),
                            interceptor.priority()
                    )
                    .build();
            println(mInterceptorTypeSpecBuilder.toString());
            println(mInterceptorTypeSpecBuilder.methodSpecs.get(0).toString());
            MethodSpec methodSpec = mInterceptorTypeSpecBuilder.methodSpecs.get(0).toBuilder().addCode(codeBlock).build();
            println(methodSpec.toString());
            mInterceptorTypeSpecBuilder.methodSpecs.clear();
            TypeSpec typeSpec1 = mInterceptorTypeSpecBuilder.addMethod(methodSpec).build();
            println("------");
            println(typeSpec1.toString());
        }
        return mInterceptorTypeSpecBuilder;
    }

    /**
     * create a java file or append content on it
     *
     * @param route   route
     * @param element type element
     */
    private TypeSpec.Builder createOrAppendRouter(Route route, TypeElement element) {
        if (!isValidRoute(route)) {
            println("route annotation is invalid, return.");
            return null;
        }
        println("type spec:");
        String group = getGroup(route);
        println("group:" + group);
        TypeSpec.Builder typeSpec = mTypeMap.get(group);
        if (typeSpec == null) {
            String className = Constants.GROUP_PREFIX + group;
            println("group:" + group + ", class:" + className);
            typeSpec = createRouter(route, className, element);
            mTypeMap.put(group, typeSpec);
            println("map put...");
        } else {
            println("map exsit");
            CodeBlock codeBlock = CodeBlock.builder()
                    .addStatement("map.put($S, new $N($S, $N.class));",
                            route.path(),
                            ComponentInfo.class.getSimpleName(),
                            route.path(),
                            element.toString()
                    )
                    .build();
            println(typeSpec.toString());
            println(typeSpec.methodSpecs.get(0).toString());
            MethodSpec methodSpec = typeSpec.methodSpecs.get(0).toBuilder().addCode(codeBlock).build();
            println(methodSpec.toString());
            typeSpec.methodSpecs.clear();
            TypeSpec typeSpec1 = typeSpec.addMethod(methodSpec).build();
            println("------");
            println(typeSpec1.toString());
        }
        return typeSpec;
    }

    private boolean isValidRoute(Route route) {
        String path = route.path();
        if (!path.startsWith("/")) {
            return false;
        }
        String[] elements = path.split("/");
        if (elements.length < 2) {
            return false;
        }
        return true;
    }

    private String getGroup(Route route) {
        if (!isValidRoute(route)) {
            return null;
        }
        return route.path().split("/")[1];
    }



    private void printClassName(ClassName className) {

        println(className.canonicalName() + "," + className.simpleName());

    }


    private void printlnKindAndModifiers(Element element) {
        println("");
        println("print kind and modifiers <<<<<<<<");
        println("simple name:" + element.getSimpleName());
        ElementKind kind = element.getKind();
        println("kind:" + kind);

        Set<Modifier> modifiers = element.getModifiers();
        for (Modifier modifier : modifiers) {
            println("modifier:" + modifier);
        }
        println("print kind and modifiers >>>>>>>>>");
        println("");
    }


    private void printAnnotationMirror(AnnotationMirror mirror) {
        println("");
        println("print annotation mirror <<<<<<<<<<<");
        println("annotation mirror class:" + mirror.getClass().toString());
        println("annotaion mirror:" + mirror);
        DeclaredType type = mirror.getAnnotationType();
        println("annotation type:" + type);
        Element annotationTypeElement = type.asElement();
        println("annotation type element:" + annotationTypeElement);
        TypeMirror annotationTypeMirror = type.getEnclosingType();
        println("annotation type mirror:" + annotationTypeMirror);
        List<TypeMirror> typeArgments = (List<TypeMirror>) type.getTypeArguments();
        for (TypeMirror typeMirror1 : typeArgments) {
            println("argument--------------");
            printMirror(typeMirror1);
        }
        println("print annotation mirror >>>>>>>>>>>");
        println("");
    }

    private void printElement(Element element) {
        println("");
        println("print element <<<<<<<<<<<<<<<");
        if (element instanceof VariableElement) {
            VariableElement variableElement = (VariableElement) element;
            printVariableElement(variableElement);
        } else if (element instanceof ExecutableElement) {
            ExecutableElement executableElement = (ExecutableElement) element;
            printExecutableElement(executableElement);
        } else if (element instanceof TypeElement) {
            TypeElement typeElement = (TypeElement) element;
            printTypeElement(typeElement);
        } else if (element instanceof TypeParameterElement) {
            TypeParameterElement typeParameterElement = (TypeParameterElement) element;
            printTypeParameterElement(typeParameterElement);
        } else {
            println("element:" + element);
        }
        println("print element >>>>>>>>>>>>>");
        println("");

    }

    private void printTypeParameterElement(TypeParameterElement element) {
        println("");
        println("print type parameter element <<<<<<<<<<");
        println("element:" + element);
        Element element1 = element.getEnclosingElement();
        println("enclosing element:" + element1);
        List<TypeMirror> bounds = (List<TypeMirror>) element.getBounds();
        for (TypeMirror mirror : bounds) {
            printMirror(mirror);
        }
        println("generic element:" + element.getGenericElement());
        printlnKindAndModifiers(element);
        println("print type parameter element >>>>>>>>>>");
    }


    private void printVariableElement(VariableElement element) {
        println("");
        println("print variable element <<<<<<<<<<<<<<<<");
        println("element:" + element);
        println("simple name:" + element.getSimpleName());
        TypeMirror mirror = element.asType();
        printMirror(mirror);
        Set<Modifier> modifiers = element.getModifiers();
        for (Modifier modifier : modifiers) {
            println("modifier:" + modifier);
        }
        Element enclosingElement = element.getEnclosingElement();
        println("enclosing element:" + enclosingElement);
        println("print variable element >>>>>>>>>>>>>>>");
        println("");
    }

    private void printExecutableElement(ExecutableElement element) {
        println("");
        println("print executable element <<<<<<<<<<<<");
        println("element:" + element);
        printlnKindAndModifiers(element);
        println("return type:");
        printMirror(element.getReturnType());
        println("receive type:");
        printMirror(element.getReceiverType());
        println("thrown types:");
        List<TypeMirror> throwTypes = (List<TypeMirror>) element.getThrownTypes();
        for (TypeMirror mirror : throwTypes) {
            printMirror(mirror);
        }
        println("type parameter types:");
        List<TypeParameterElement> typeParameters = (List<TypeParameterElement>) element.getTypeParameters();
        for (TypeParameterElement typeParameter : typeParameters) {
            printElement(typeParameter);
        }
        println("parameters:");
        List<VariableElement> parameterElements = (List<VariableElement>) element.getParameters();
        for (VariableElement parameter : parameterElements) {
            printVariableElement(parameter);
        }

        println("print executable element >>>>>>>>>>>>>>");

    }

    private void printTypeElement(TypeElement element) {
        println("printTypeElement <<<<<<<<<<<<");
        TypeMirror typeMirror = element.asType();
        println("typeMirror:" + typeMirror);
        List<TypeMirror> interfaces = (List<TypeMirror>) ((TypeElement) element).getInterfaces();
        for (TypeMirror interface1 : interfaces) {
            println("interface--------------");
            printMirror(interface1);
        }
        // 注解的值
        println("get annotation on Type--------");
        Route annotation = element.getAnnotation(Route.class);
        println("annotation:" + annotation);
        println("route , path:" + annotation.path() + ", name:" + annotation.name());
        // 注解类型
        List<AnnotationMirror> annotationMirrors = (List<AnnotationMirror>) element.getAnnotationMirrors();
        for (AnnotationMirror mirror : annotationMirrors) {
            printAnnotationMirror(mirror);
        }

        //
        printlnKindAndModifiers(element);

        List<Element> elementList = (List<Element>) element.getEnclosedElements();
        for (Element el : elementList) {
            printElement(el);
        }
        println("printTypeElement >>>>>>>>>>>>");
    }

    private void printMirror(TypeMirror mirror) {
        if (mirror == null) return;
        println("printMirror <<<<<<<<<<<<");
        println("mirror:" + mirror);
        println("kind:" + mirror.getKind().name());
        println("printMirror >>>>>>>>>>>>");
    }

    private void println(String msg) {
        System.out.println(msg);
    }
}
