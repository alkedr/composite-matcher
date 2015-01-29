package com.github.alkedr.matchers.reporting;

import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.IOException;
import java.io.PrintWriter;

import static org.apache.commons.io.IOUtils.toByteArray;

/**
 * User: alkedr
 * Date: 28.01.2015
 */
public class IntersTest {

    private class Bean {
        int x = 4;
    }

    private interface X {
        Object f(Bean bean);
    }


    public static byte[] getClassBytecode(Class<?> clazz) throws IOException {
//        Class<?> enclosingClass = clazz;
//        while (enclosingClass.isMemberClass() || enclosingClass.isAnonymousClass()){
//            enclosingClass = enclosingClass.getEnclosingClass();
//        }
        return toByteArray(clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class"));
    }


    @Test
    public void testName() throws Exception {
        X x = new X() {
            @Override
            public Object f(Bean bean) {
                return bean.x;
            }
        };

        /*ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get(x.getClass().getName());

        CodeIterator iterator = cc.getMethods()[0].getMethodInfo().getCodeAttribute().iterator();
        while (iterator.hasNext()) {
            try {
                int pos = iterator.next();
                System.out.println(pos);
            }
            catch (BadBytecode e) {
                throw new CannotCompileException(e);
            }
        }*/


        Textifier textifier = new Textifier();
        ClassVisitor cv = new ValueExtractorClassVisitor(textifier);
        new ClassReader(getClassBytecode(x.getClass())).accept(cv, 0);
        textifier.print(new PrintWriter(System.out));
    }



    private static class ValueExtractorClassVisitor extends ClassVisitor {
        private final Textifier textifier;

        private ValueExtractorClassVisitor(Textifier textifier) {
            super(Opcodes.ASM5);
            this.textifier = textifier;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            System.out.println(name);
            return name.equals("f") ? new TraceMethodVisitor(textifier) : null;
//            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
}
