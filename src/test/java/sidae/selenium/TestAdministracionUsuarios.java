package sidae.selenium;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import testwatchers.Reports;

@ExtendWith(Reports.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
/**
 * Test general que crea un usuario nuevo desde un usuario administrador si no existe en la bbdd
 * luego el nuevo usuario entra y actualiza su  perfil
 * y por ultimo el usuario administrador borra el nuevo usuario registrado
 */
public class TestAdministracionUsuarios {
	private static WebDriver driver;

	@BeforeAll
	public static void preEjecucionTest() throws Exception {
		driver = new ChromeDriver();
		
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
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

	/**
	 * Este metodo da de alta a un usuario nuevo desde un usuario administrador
	 *  y verifica que se ha dado de alta 
	 * @throws Exception
	 */
	@Order(10)
	@Test
	public void testAltaUsuarioDesdeAdministrador() throws Exception {
		// Crea un objeto JavascriptExecutor
	      JavascriptExecutor js = (JavascriptExecutor) driver;
		
		WebElement btnAdministrar = driver.findElement(By.id("documento:administrarUsuarios"));
		btnAdministrar.click();

		WebElement nombre = driver.findElement(By.id("documento:coUsuarios"));
		nombre.sendKeys("Rober");
		
		js.executeScript("alert('Usuario Rober registrado');");
		
		Alert alert = driver.switchTo().alert();
		String msj = alert.getText();
		alert.accept();
		WebElement nombreCompleto = driver.findElement(By.id("documento:dsUsuarios"));
		nombreCompleto.sendKeys("Roberto");
		WebElement contraseña = driver.findElement(By.id("documento:dsPassword"));
		contraseña.sendKeys("12345");
		WebElement contraseñaR = driver.findElement(By.id("documento:dsPasswordR"));
		contraseñaR.sendKeys("12345");
		WebElement correo = driver.findElement(By.id("documento:email"));
		correo.sendKeys("prueba@prueba.es");
		WebElement usuarioBd = driver.findElement(By.id("documento:usuarioBd"));
		usuarioBd.sendKeys("eder");

		WebElement btnRegistrar = driver.findElement(
				By.xpath("//span[contains(@class, 'ui-button-text') and text()='Registrar nuevo usuario']"));
		btnRegistrar.click();
		
		WebElement popup = driver.findElement(By.xpath("//*[contains(@class, 'ui-message-dialog') and contains(., 'Exito')]"));
		Assertions.assertTrue(popup.isDisplayed(), "El mensaje de exito se muestra correctamente.");
	}

	/**
	 * Este test modifica los datos del usuario y comprueba que las contraseñas coincidan
	 * y que el correo está bien escrito.
	 */
	@Order(20)
	@Test
	public void testEditarUsuarioNuevo() {
		driver.navigate().to("http://localhost:92/hello_jsf23/filtroFacturas.xhtml");
		WebElement cerrarSesion = driver.findElement(By.xpath("//a[contains(@onclick, 'mojarra.jsfcljs') and contains(@href, '#')]/img[@alt='Cerrar Sesión']"));
		cerrarSesion.click();
		WebElement user = driver.findElement(By.id("documento:username"));
		user.sendKeys("Rober");
		WebElement password = driver.findElement(By.id("documento:password"));
		password.sendKeys("12345");
		WebElement button = driver.findElement(By.id("documento:j_idt10"));
		button.click();
		WebElement btnPerfil = driver.findElement(By.xpath("//a[contains(@onclick, 'mojarra.jsfcljs') and contains(@href, '#')]/img[@alt='Perfil de usuario']"));
		btnPerfil.click();
		
		WebElement nombreCompleto = driver.findElement(By.id("documento:dsUsuarios"));
		nombreCompleto.clear();
		nombreCompleto.sendKeys("Roberto máquina");
		WebElement contraseña = driver.findElement(By.id("documento:dsPassword"));
		contraseña.sendKeys("123456");
		WebElement contraseñaR = driver.findElement(By.id("documento:dsPasswordR"));
		contraseñaR.sendKeys("123456");
		
		String passwordValue = contraseña.getAttribute("value");
		String confirmPasswordValue = contraseñaR.getAttribute("value");

		// Verificar si las contraseñas coinciden
		if (!passwordValue.equals(confirmPasswordValue)) {
		    // Si las contraseñas no coinciden, lanzar una aserción
		    Assertions.fail("Las contraseñas no coinciden.");
		}
		
		WebElement correo = driver.findElement(By.id("documento:email"));
		correo.clear();
		correo.sendKeys("rober@gmail.es");
		String emailValue = correo.getAttribute("value");
		// Verificar el formato del correo electrónico utilizando una expresión regular
		boolean formatoCorrecto = emailValue.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
		// Verificar si el formato es correcto
		Assertions.assertTrue(formatoCorrecto, "El correo electrónico no está en el formato correcto.");
		
		WebElement btnEditarPerfil = driver.findElement(By.xpath("//*[contains(@class, 'ui-button-text ui-c') and contains(., 'Editar Perfil')]"));
		btnEditarPerfil.click();
	}
	
	/**
	 * Aqui eliminamos al usuario que acabamos de dar de alta y de editar desde 
	 * el usuario administrador
	 */
	
	@Order(30)
	@Test
	public void testEliminarUsuarioNuevoDesdeAdmin() {
		driver.navigate().to("http://localhost:92/hello_jsf23/filtroFacturas.xhtml");
		WebElement cerrarSesion = driver.findElement(By.xpath("//a[contains(@onclick, 'mojarra.jsfcljs') and contains(@href, '#')]/img[@alt='Cerrar Sesión']"));
		cerrarSesion.click();
		WebElement user = driver.findElement(By.id("documento:username"));
		user.sendKeys("ferlloren");
		WebElement password = driver.findElement(By.id("documento:password"));
		password.sendKeys("12345");
		WebElement button = driver.findElement(By.id("documento:j_idt10"));
		button.click();
		WebElement btnAdministrar = driver.findElement(By.id("documento:administrarUsuarios"));
		btnAdministrar.click();
		WebElement btnEliminarUsuario = driver.findElement(By.xpath("//tr[contains(td, 'Rober')]//button[contains(@id, 'j_idt32')]"));
		btnEliminarUsuario.click();
		WebElement btnAceptar = driver.findElement(By.xpath("//span[contains(@class, 'ui-button-text ui-c') and contains(text(), 'Aceptar')]"));
		btnAceptar.click();
		WebElement popup = driver.findElement(By.xpath("//*[contains(@class, 'ui-message-dialog') and contains(., 'Enhorabuena')]"));
		Assertions.assertTrue(popup.isDisplayed(), "El mensaje de error se muestra correctamente.");
	}
	
	
	
//	@Test
//	public void testPruebaJs() {
//		// Crea un objeto JavascriptExecutor
//        JavascriptExecutor js = (JavascriptExecutor) driver;
//
//        // Ejecuta JavaScript utilizando executeScript()
//        // Por ejemplo, puedes cambiar el color de fondo del cuerpo de la página a rojo
//        js.executeScript("document.body.style.backgroundColor = 'red';");
//
//        // También puedes ejecutar JavaScript con retorno de valor
//        // Por ejemplo, puedes obtener el título de la página
//        String title = (String) js.executeScript("return document.title;");
//        System.out.println("El título de la página es: " + title);
//	}
//	

	public static List<WebElement> waitFor(WebDriver driver, By by) {
		WebDriverWait exists = new WebDriverWait(driver, Duration.ofSeconds(5));
		exists.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
		return driver.findElements(by);
	}
}
