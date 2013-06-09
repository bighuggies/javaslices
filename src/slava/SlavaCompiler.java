package slava;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.visitor.DumpVisitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import slava.scope.Scope;
import slava.scope.Scope.ScopeType;
import slava.visitor.DefinitionVisitor;
import slava.visitor.ResolutionVisitor;
import slava.visitor.ScopeVisitor;
import slava.visitor.TypingVisitor;

public class SlavaCompiler {

	/**
	 * @param args
	 */
	public static void main(String... args) {
		try {
			File[] files = new File[args.length];

			for (int i = 0; i < args.length; i++) {
				String filePath = args[i];
				String[] pathParts = filePath.split("\\.");

				if (pathParts.length < 2 || !pathParts[pathParts.length - 1].equals("javax")) {
					System.out.println("Please specify files ending in \".javax\"");
					continue;
				}

				files[i] = loadFile(args[i]);
			}

			for (File f : files) {
				if (f == null)
					continue;

				CompilationUnit ast = parseFile(f);
				String compiled = compileFile(ast);
				writeCompiledFile(Paths.get(f.getAbsolutePath().replace(".javax", ".java")), compiled);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

	}

	private static void writeCompiledFile(Path p, String source) throws IOException {
		FileWriter fr = new FileWriter(p.toFile());
		fr.write(source);
		fr.close();

		System.out.println(source);
	}

	private static final String compileFile(CompilationUnit tree) {
		ScopeVisitor<Object> sv = new ScopeVisitor<Object>(new Scope(null, ScopeType.GLOBAL, "global"));
		TypingVisitor<Object> tv = new TypingVisitor<Object>();
		DefinitionVisitor<Object> dv = new DefinitionVisitor<Object>();
		ResolutionVisitor rv = new ResolutionVisitor();
		DumpVisitor dumpv = new DumpVisitor();

		tree.accept(sv, null);
		tree.accept(tv, null);
		tree.accept(dv, null);
		tree.accept(rv, null);
		tree.accept(dumpv, null);

		return dumpv.getSource();
	}

	private static final CompilationUnit parseFile(File file) throws ParseException, IOException {
		return JavaParser.parse(file);
	}

	private static final File loadFile(String path) throws IOException {
		Path p = Paths.get(new File(".").getCanonicalPath(), path);

		if (Files.exists(p)) {
			return p.toFile();
		} else {
			throw new IOException("Path " + p.toString() + " does not exist");
		}
	}

}
