package sidae.serviciostecnicos.usuarios;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import sidae.jpa.beans.usuarios.BdDUsuarios;

public class StDUsuariosTest {

	@Test
	void testIsExisteCodigo() {
		fail("Not yet implemented");
	}
	
	public static Stream<Arguments> devolverUsuarios() {
		return Stream.of(
			      Arguments.of("ferlloren", 1, true),
			      Arguments.of("oscar", 0, true),
			      Arguments.of("Oscar", 0, true),
			      Arguments.of("oscarlloren", 0, false),
			      Arguments.of(null, 0, true),
			      Arguments.of("", 0, true),
			      Arguments.of("   ", 0, false)
			    );
	}
	
	
	@ParameterizedTest
	@MethodSource("devolverUsuarios")
	public void testFiltroDevuelveUsuarios(String usuario, int admin, boolean existeUsuario) throws Exception {
		StDUsuarios stUsuarios = new StDUsuarios();
		BdDUsuarios bdDUsuarios = new BdDUsuarios();
		bdDUsuarios.setBoAdmin(admin);
		bdDUsuarios.setCoUsuarios(usuario);
		List<BdDUsuarios> filtro = stUsuarios.filtro(bdDUsuarios);
		if(existeUsuario) {
			Assertions.assertTrue(!filtro.isEmpty(), "error, el filtro debería devolver algún valor");
		} else {
			Assertions.assertTrue(filtro.isEmpty(), "error, el filtro NO debería devolver ningún valor");
		}
	}
	
	@Test
	public void testExisteUsuarioAdministradorFerlloren() throws Exception {
		StDUsuarios stUsuarios = new StDUsuarios();
		BdDUsuarios bdDUsuarios = new BdDUsuarios();
		bdDUsuarios.setBoAdmin(1);
		bdDUsuarios.setCoUsuarios("ferlloren");
		List<BdDUsuarios> filtro = stUsuarios.filtro(bdDUsuarios);
		Assertions.assertTrue(!filtro.isEmpty(), "error, no existe el usuario ferlloren");
	}

}
