package common.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileFactory {

	public static void main(String[] args) {
		// for demo
		try {
			// Absolute path demo
			String path = "C:/Users/User/git/common-functions/src/main/resources/";
			String filename = "hello_world.txt";
			String fullPath = path + filename;
			System.out.println("Full path of absolute: " + fullPath);
			// Relative path from resources folder demo
			//ClassLoader cl = ClassName.class.getClassLoader();
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			fullPath = cl.getResource(filename).getFile();
			System.out.println("Full path of relative from resources folder: " + fullPath);
			// Verify the file path demo
			File file = new File(fullPath);
			fullPath = file.getAbsolutePath();
			System.out.println("Full path of verified from file object: " + fullPath);
			// Print out the content from reading file
			String fileContent = readFile(fullPath, true);
			System.out.println("File content: " + fileContent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// read file
	public static String readFile(String path, boolean excludeComment) throws Exception {
		StringBuilder sb = new StringBuilder();
		Path p = Paths.get(path);
		String line;
		try {
			BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8);
			while ((line = br.readLine()) != null) {
				if (excludeComment) {
					if (line.startsWith("//")) {
						// do nothing
					} else {
						sb.append(line).append("\n");	
					}
				} else {
					sb.append(line).append("\n");
				}
			}
		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
		return sb.toString();
	}

}
