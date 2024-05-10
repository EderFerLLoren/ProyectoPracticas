package sidae.selenium;

import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import executioncontiditions.MyExecutionCondition;
import testwatchers.Reports;
import org.openqa.selenium.WebDriver;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(Reports.class)
@ExtendWith(MyExecutionCondition.class)
public class TestLoginUsuarios {
	
	/*@RegisterExtension
	public TestWatcher testWatcher = new Reports();*/
	
	private static WebDriver driver;

	@BeforeAll
	public static void preEjecucionTest() throws Exception {
		driver = new ChromeDriver();
		driver.navigate().to("http://localhost:92/hello_jsf23/");
		System.out.println("Ejecuto antes de ningún test @BeforeAll");

		WebElement user = driver.findElement(By.id("documento:username"));
		user.sendKeys("ferlloren");
		WebElement password = driver.findElement(By.id("documento:password"));
		password.sendKeys("12345");
		WebElement button = driver.findElement(By.id("documento:j_idt10"));
		button.click();
	}

	@BeforeEach
	public void navegarMenuInicio() {
		driver.navigate().to("http://localhost:92/hello_jsf23/filtroFacturas.xhtml");
	}

	@AfterAll
	public static void postEjecucionTest() throws Exception {
		driver.quit();
		System.out.println("Ejecuto después de todos los test @AfterAll");
	}

//	@Test
//	public void testAccesoApp() throws Exception {
//        driver.navigate().to("http://localhost:92/hello_jsf23/");
//        WebElement user = driver.findElement(By.id("documento:username"));
//        user.sendKeys("ferlloren");
//        WebElement password = driver.findElement(By.id("documento:password"));
//        password.sendKeys("12345");
//        WebElement button = driver.findElement(By.id("documento:j_idt10"));
//        button.click();
//        
//	}

//	@Test
//	public void testMuestraPopupInformativoLoginOPasswordIncorrecto() throws Exception {
//		driver.navigate().to("http://localhost:92/hello_jsf23/");
//		WebElement user = driver.findElement(By.id("documento:username"));
//		user.sendKeys("ferlloren");
//		WebElement password = driver.findElement(By.id("documento:password"));
//		password.sendKeys("123456");
//		WebElement button = driver.findElement(By.id("documento:j_idt10"));
//		button.click();		
//		List<WebElement> popup = waitFor(driver, By.id("primefacesmessagedlg"));
//		Assertions.assertTrue(!popup.isEmpty(),	"Error, no se ha mostrado un popup informativo");
//		Assertions.assertTrue(popup.get(0).getText().contains("El nombre o la contraseña no coinciden"),
//				"Error, el mensaje mostrado es incorrecto");
//	}

	@Test
	@Order(10)
	public void testAltaFactura() throws Exception {

		WebElement nuevaFactura = driver.findElement(By.name("documento:j_idt14"));
		nuevaFactura.click();	
		
		WebElement codigo = driver.findElement(By.id("documento:codigoFactura"));
		codigo.sendKeys("FactPrueba1");
		
		WebElement descripcion = driver.findElement(By.id("documento:descripcionFactura"));
		descripcion.sendKeys("Factura Prueba 1");
		
		WebElement fecha = driver.findElement(By.id("documento:fechaFactura"));
		fecha.sendKeys("07/05/2024");
		
		String fechaIngresada = fecha.getAttribute("value");
		String formatoFecha = "\\d{2}/\\d{2}/\\d{4}";
		boolean formatoCorrecto = fechaIngresada.matches(formatoFecha);
		Assertions.assertTrue(formatoCorrecto, "Error, el formato de la fecha debe ser dd/mm/aaaa");
		
		WebElement importe = driver.findElement(By.id("documento:importeFactura"));
		importe.sendKeys("1");
		double valorImporte = Double.parseDouble(importe.getAttribute("value"));
		Assertions.assertTrue(valorImporte > 0, "Error, El importe debe ser mayor que 0");
		
		WebElement usuarioBd = driver.findElement(By.id("documento:usuarioBd"));
		usuarioBd.sendKeys("Eder");
		
		// Encontrar el elemento del combo
        WebElement tipoImpuesto= driver.findElement(By.id("documento:tipoImpuesto"));
        Select impuestoSeleccionado = new Select(tipoImpuesto);
        impuestoSeleccionado.selectByValue("1");
        
        WebElement grabar = driver.findElement(By.name("documento:j_idt24"));
        grabar.click();
        System.out.println("Grabado correctamente");
	}

	/**
	 * test que comprueba los campos obligatorios en modo edición de las facturas
	 * 
	 * @throws Exception
	 */
	//@Disabled("deshabilitado por petición del usuario")
	@Test
	@Order(20)
	public void testCompruebaCamposGrabarFactura() throws Exception {
	    WebElement filtrarFacturas = driver.findElement(By.id("documento:filtrarFactura"));
	    filtrarFacturas.click();
	    WebElement primerLink = driver.findElement(By.linkText("Factura nueva"));
	    primerLink.click();

	    WebElement codigo = driver.findElement(By.id("documento:codigoFactura"));
	    Assertions.assertFalse(codigo.getAttribute("value").isEmpty(), "Error, el campo de código de factura no puede estar vacío");

	    WebElement descripcion = driver.findElement(By.id("documento:descripcionFactura"));
	    Assertions.assertFalse(descripcion.getAttribute("value").isEmpty(), "Error, el campo de descripción de factura no puede estar vacío");

	    WebElement fecha = driver.findElement(By.id("documento:fechaFactura"));
	    Assertions.assertFalse(fecha.getAttribute("value").isEmpty(), "Error, el campo de fecha de factura no puede estar vacío");

	    WebElement importe = driver.findElement(By.id("documento:importeFactura"));
	    Assertions.assertFalse(importe.getAttribute("value").isEmpty(), "Error, el campo de importe de factura no puede estar vacío");

	    WebElement usuarioBd = driver.findElement(By.id("documento:usuarioBd"));
	    Assertions.assertFalse(usuarioBd.getAttribute("value").isEmpty(), "Error, el campo de usuario de base de datos no puede estar vacío");

	    // Verificación del campo total (importeBaseImponible)
	    WebElement popup = driver.findElement(By.id("documento:importeBaseImponible"));
	    Assertions.assertFalse(popup.getText().contains("Error"), "Error, el campo de importe base imponible no puede estar vacío");

	    System.out.println("Campos rellenados");
	}

	/**
	 * consulta la factura y comprueba que los formatos de los campos fecha e
	 * importe son correctos y modifico los valores de los campos, grabándolos.
	 * 
	 * @throws Exception
	 */
	@Test
	@Order(30)
	public void testModificarFactura() throws Exception {
		WebElement fecha = driver.findElement(By.id("documento:fechaFactura"));
		String fechaIngresada = fecha.getAttribute("value");
		String formatoFecha = "\\d{2}/\\d{2}/\\d{4}";
		boolean formatoCorrecto = fechaIngresada.matches(formatoFecha);
		Assertions.assertTrue(formatoCorrecto || fechaIngresada.isEmpty(),
				"Error, el formato de la fecha debe ser dd/mm/aaaa y no puede estar vacía.");

		WebElement importe = driver.findElement(By.id("documento:importeFactura"));
		String valorImporteStr = importe.getAttribute("value");
		Assertions.assertFalse(valorImporteStr.isEmpty(), "Error, El importe no puede estar vacío");
		double valorImporte = Double.parseDouble(importe.getAttribute("value"));
		Assertions.assertTrue(valorImporte > 0, "Error, El importe debe ser mayor que 0");
	}

	@Test
	public void testBorrarFactura() throws Exception {
		WebElement filtrarFacturas = driver.findElement(By.id("documento:filtrarFactura"));
	    filtrarFacturas.click();
	    WebElement primerLink = driver.findElement(By.linkText("Factura nueva"));
	    primerLink.click();
	    WebElement borrarBoton = driver.findElement(By.id("documento:eliminarFactura"));
	    borrarBoton.click();
	}

	/**
	 * 
	 * @param driver
	 * @param by
	 * @return
	 */
	public static List<WebElement> waitFor(WebDriver driver, By by) {
		WebDriverWait exists = new WebDriverWait(driver, Duration.ofSeconds(5));
		exists.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
		return driver.findElements(by);
	}
	
	
	
	

}
