package jp.go.ndl.lab.annotation.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jp.go.ndl.lab.annotation.domain.Action;
import jp.go.ndl.lab.annotation.domain.ImageBinder;
import jp.go.ndl.lab.annotation.domain.ImageType;
import jp.go.ndl.lab.annotation.domain.TargetImage;
import org.apache.commons.text.StrBuilder;

public class TypeDefinitionTool {

    public static void main(String[] args) throws Exception {
        print(TargetImage.class);
        print(Action.class);
        print(ImageBinder.class);
        print(ImageType.class);
    }
    static Path base = Paths.get("web\\src\\domain");

    static Set<Class> classes = new LinkedHashSet<>();
    static Set<Class> enums = new LinkedHashSet<>();

    static Set<String> exField = new HashSet<>(Arrays.asList("", ""));

    private static StrBuilder sb = new StrBuilder();

    static void println(String s) {
        sb.appendln(s);
    }

    static void print(String s) {
        sb.append(s);
    }

    static void print(Class clazz, Class... exclazz) throws Exception {
        classes.clear();
        enums.clear();
        sb = new StrBuilder();
        search(clazz);

        for (Class c : exclazz) {
            sb.insert(0, "import {" + c.getSimpleName() + "} from \"./" + c.getSimpleName().toLowerCase() + "\"\n");
            classes.remove(c);
        }

        lp(clazz);

        for (Class sub : classes) {
            lp(sub);
        }

        for (Class em : enums) {
            genenum(em);
        }

        Files.write(base.resolve(clazz.getSimpleName().toLowerCase() + ".ts"), sb.toString().getBytes(StandardCharsets.UTF_8));

    }

    static void search(Class clazz) {
        loop:
        for (Field f : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            if (exField.contains(f.getName())) continue;

            Class fType = f.getType();

            if (fType.isEnum()) {
                enums.add(f.getType());
            } else {
                if (fType == List.class) {
                    ParameterizedType paramType = (ParameterizedType) f.getGenericType();
                    for (java.lang.reflect.Type i : paramType.getActualTypeArguments()) {
                        fType = (Class) i;
                    }
                } else if (fType == Map.class) {
                    ParameterizedType paramType = (ParameterizedType) f.getGenericType();
                    for (java.lang.reflect.Type i : paramType.getActualTypeArguments()) {
                        if (!(i instanceof Class)) {
                            continue loop;
                        }
                        fType = (Class) i;
                    }
                }
                String tst = tsType(fType);
                if (tst == null) {
                    search(fType);
                    classes.add(fType);
                }
            }
        }
    }

    static void lp(Class clazz) throws Exception {

        print("export interface " + clazz.getSimpleName());
        println("{");
        loop:
        for (Field f : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            if (exField.contains(f.getName())) continue;

            Class fType = f.getType();
            if (fType == List.class) {
                ParameterizedType paramType = (ParameterizedType) f.getGenericType();
                for (java.lang.reflect.Type i : paramType.getActualTypeArguments()) {
                    fType = (Class) i;
                }
                String tst = tsType(fType);
                if (tst == null) {
                    tst = fType.getSimpleName();
                }
                println(f.getName() + "?:" + tst + "[];");
            } else if (f.getType().isArray()) {
                fType = fType.getComponentType();
                String tst = tsType(fType);
                if (tst == null) {
                    tst = fType.getSimpleName();
                }
                println(f.getName() + "?:" + tst + "[];");
            } else if (fType.isEnum()) {
                println(f.getName() + "?:" + fType.getSimpleName() + ";");
            } else if (fType == Map.class) {
                ParameterizedType paramType = (ParameterizedType) f.getGenericType();
                for (java.lang.reflect.Type i : paramType.getActualTypeArguments()) {
                    if (!(i instanceof Class)) {
                        continue loop;
                    }
                    fType = (Class) i;
                }
                String tst = tsType(fType);
                if (tst == null) {
                    tst = fType.getSimpleName();
                }
                println(f.getName() + "?:{[key:string]:" + tst + "};");
            } else {
                String tst = tsType(fType);
                if (tst == null) {
                    tst = fType.getSimpleName();
                }
                println(f.getName() + "?:" + tst + ";");
            }
        }
        println("}");
    }

    static String tsType(Class clazz) {
        switch (clazz.getSimpleName()) {
            case "Date":
                return "number";
            case "int":
                return "number";
            case "long":
                return "number";
            case "Long":
                return "number";
            case "Integer":
                return "number";
            case "Double":
                return "number";
            case "double":
                return "number";
            case "String":
                return "string";
            case "Boolean":
                return "boolean";
            case "boolean":
                return "boolean";
            default:
                return null;
        }

    }

    /**
     * クラスをEnumに変換して標準出力する。
     *
     * @param code クラス型
     */
    public static void genenum(Class code) throws Exception {
        String name = code.getSimpleName();
        Map<String, String> codeMap = new HashMap<>();

        for (Object c : code.getEnumConstants()) {
            Enum e = (Enum) c;
            codeMap.put(e.name(), "null");
        }

        print("export type " + name + " = ");
        println(codeMap.keySet().stream().map(str -> "\"" + str + "\"").collect(Collectors.joining("|")) + ";");
    }

}
