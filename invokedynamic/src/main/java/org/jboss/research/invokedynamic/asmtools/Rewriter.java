package org.jboss.research.invokedynamic.asmtools;

import org.jboss.research.invokedynamic.Container;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * This Class use ASM to rewrite class that manipulates class with {@link org.jboss.research.invokedynamic.Interceptable}
 * annotation. The call to such class will be rewrite with invokde dynamic
 *
 */
public class Rewriter {


    /**
     * A handle to the InvokeDynamic bootstrap method needed to initialize bootstrap calls it points to {@link org.jboss
     * .research.invokedynamic.Container#bootstrap} method
     */
    private static final Handle BSM = new Handle(Opcodes.H_INVOKESTATIC,
            Container.class.getName().replace('.', '/'),
            "bootstrap",
            MethodType.methodType(CallSite.class, Lookup.class, String.class, MethodType.class,
                    MethodHandle.class).toMethodDescriptorString());


    /**
     *
     * Return a Bytecode Tag for handle from an opcode. If the opcode is not in the one tested return 0 as a tag handle
     *
     * @param opcode to return the tag handle for
     * @return the corresponding tag handle
     */
    static int asMethodHandleTag(int opcode) {
        switch (opcode) {
            case Opcodes.GETFIELD:
                return Opcodes.H_GETFIELD;
            case Opcodes.PUTFIELD:
                return Opcodes.H_PUTFIELD;
            case Opcodes.INVOKESTATIC:
                return Opcodes.H_INVOKESTATIC;
            case Opcodes.INVOKEVIRTUAL:
                return Opcodes.H_INVOKEVIRTUAL;
            case Opcodes.INVOKEINTERFACE:
                return Opcodes.H_INVOKEINTERFACE;
            default:
                return 0;  // not a trapped opcode
        }
    }


    // Code weaving
    public static void main(String[] args) throws IOException {
        Path directory = Paths.get(args[0]); // get the directory sent in parameter
        System.out.println("rewrite directory " + directory);


        final Map<ClassReader, Path> reader2Path = new HashMap<>();
        final Set<String> annotatedTypes = new HashSet<>();

        // We visit the directory and create an ASM reader for each class file and keep file and reader associated in
        // reader2path map
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".class")) {
                    ClassReader reader = new ClassReader(Files.newInputStream(file));
                    reader2Path.put(reader, file);
                    // we check if class has an @Intercpetable anootation an keep it in a list toknwo which class should be
                    // call in invokedynamic.
                    if (InterceptedClassVisitor.isIntercepted(reader))
                        annotatedTypes.add(reader.getClassName().replace('.', '/'));
                }
                return FileVisitResult.CONTINUE;
            }
        });


        System.out.println("found annotated types " + annotatedTypes);


        // We walk thru all the class of the application. We have to check if these class use classes having @Interceptable
        // annotaion to change the static call the these class ofr an InvokeDynamic one
        for (final ClassReader classReader : reader2Path.keySet()) {
            final ClassWriter classWriter = new ClassWriter(classReader, 0);
            classReader.accept(new ClassVisitor(Opcodes.ASM5, classWriter) {
                boolean modified;

                // We visit each method...
                @Override
                public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
                    return new MethodVisitor(Opcodes.ASM5, mv) {

                        // ... and in each method we visit instruction
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String desc) {
                            int methodHandleTag;
                            // if we call an instruction belonging to a class @Interceptable and if the bytecode operation is
                            // known, we replace the static instruction by invokedynamic.
                            if (annotatedTypes.contains(owner) && (methodHandleTag = asMethodHandleTag(opcode)) != 0) {
                                Handle handle = new Handle(methodHandleTag, owner, name, desc);
                                String indyDesc = '(' + ((owner.charAt(0) == '[') ? owner : 'L' + owner + ';') + desc
                                        .substring(1);
                                visitInvokeDynamicInsn(name, indyDesc, BSM, handle); // Invokedynamic replacement we send a
                                // pointer to a boostrap method that will be used to initialize the dynamic call the first
                                // time.
                                modified = true;
                                return;
                            }
                            super.visitMethodInsn(opcode, owner, name, desc);
                        }

                        // We do the same for field instruction
                        @Override
                        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                            int methodHandleTag;
                            if (annotatedTypes.contains(owner) && (methodHandleTag = asMethodHandleTag(opcode)) != 0) {
                                Handle handle = new Handle(methodHandleTag, owner, name, desc);
                                String indyDesc = '(' + ((owner.charAt(0) == '[') ? owner : 'L' + owner + ';') + ')' + desc;
                                visitInvokeDynamicInsn(name, indyDesc, BSM, handle);
                                modified = true;
                                return;
                            }
                            super.visitFieldInsn(opcode, owner, name, desc);
                        }
                    };
                }

                @Override
                public void visitEnd() {
                    super.visitEnd();
                    if (modified) {  // if bytecode was modified we write the modified class file to the disk
                        try {
                            Path path = reader2Path.get(classReader);
                            Files.write(path, classWriter.toByteArray());
                            System.out.println("rewrite " + path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }, 0);
        }
    }
}
