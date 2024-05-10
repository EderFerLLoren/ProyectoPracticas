package testwatchers;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

/**
 * clase encargada de guardar en un fichero csv los resultados 
 * de los test
 */
public class Reports implements TestWatcher  {
	String csvFile = "data.csv"; // Nombre del archivo CSV
	
	@Override
	public void testDisabled(ExtensionContext context, Optional<String> reason) {
		try(FileWriter fileWriter = new FileWriter(csvFile, true);
			PrintWriter printWriter = new PrintWriter(fileWriter)) {
	        printWriter.println(String.join(",", "DISABLED", getTestName(context), sanitize(reason.get())));
		} catch (IOException e) {
			e.printStackTrace();
		} 
        
	}
	
	@Override
	public void testSuccessful(ExtensionContext context) {
		try(FileWriter fileWriter = new FileWriter(csvFile, true);
				PrintWriter printWriter = new PrintWriter(fileWriter)) {
		        printWriter.println(String.join(",", "SUCCESS", getTestName(context), ""));
			} catch (IOException e) {
				e.printStackTrace();
			} 
	}
	
	@Override
	public void testAborted(ExtensionContext context, Throwable cause) {
		try(FileWriter fileWriter = new FileWriter(csvFile, true);
				PrintWriter printWriter = new PrintWriter(fileWriter)) {
		        printWriter.println(String.join(",", "ABORTED", getTestName(context),  sanitize(cause.getMessage())));
			} catch (IOException e) {
				e.printStackTrace();
			} 
	}
	
	@Override
	public void testFailed(ExtensionContext context, Throwable cause) {
		try(FileWriter fileWriter = new FileWriter(csvFile, true);
				PrintWriter printWriter = new PrintWriter(fileWriter)) {
		        printWriter.println(String.join(",", "FAILED", getTestName(context), sanitize(cause.getMessage())));
			} catch (IOException e) {
				e.printStackTrace();
			} 
	}
	
	
	
	private String getTestName(ExtensionContext context) {
		return sanitize(context.getRequiredTestInstance().getClass().getName() + "." + context.getDisplayName());
	}
	
	private String sanitize(String str) {
		return str.replace(",", " ").replace("\n", " ");
	}
	
	

}
