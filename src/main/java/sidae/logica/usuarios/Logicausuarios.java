package sidae.logica.usuarios;


import java.sql.SQLException;
import java.text.ParseException;
import sidae.jpa.beans.usuarios.BdDUsuarios;
import sidae.serviciostecnicos.usuarios.StDUsuarios;

public class Logicausuarios {
	private StDUsuarios stUsuarios = new StDUsuarios();
	
	private void alta(BdDUsuarios usuario) throws SQLException, ParseException { 
		if(usuario != null && stUsuarios.isExisteCodigo(usuario.getCoUsuarios())) {
			System.out.println("Ya existe ese código de usuario");			
		}else {			
			stUsuarios.alta(usuario);			
		}				
	}
	
	public void grabar(BdDUsuarios objeto) throws SQLException, ParseException {
	 
//	    if(objeto.getIdUsuarios() != null ) { 		    
//		    
//	    }
		
	    if (objeto.getIdUsuarios() == null && !stUsuarios.isExisteCodigo(objeto.getCoUsuarios())) { 
	    	//Usuario nulo:	
	        System.out.println("Damos de alta");
	        this.alta(objeto);
	    }else {
	        System.out.println("Actualizo");
	        stUsuarios.actualiza(objeto);
	    }
	}
	
	public void baja(BdDUsuarios objeto) throws SQLException{
		if(objeto == null) {
			System.out.println("No hay usuario para borrar");			
		}else {			
			stUsuarios.baja(objeto);			
		}
	}
	
	public BdDUsuarios item(String coUsuarios) throws SQLException, ParseException {		
		BdDUsuarios item = stUsuarios.item(coUsuarios);
		return item;		
	}
	
	public boolean verificarExistenciaCodigo(BdDUsuarios objeto) throws SQLException, ParseException {
	    return objeto.getIdUsuarios() == null && stUsuarios.isExisteCodigo(objeto.getCoUsuarios());
	}
	
	public boolean iniciarSesion(String nombreUsuario, String contraseña) {
	    StDUsuarios stDUsuarios = new StDUsuarios();

	    try {
	        // Verificar las credenciales en la base de datos utilizando el método verificarCredenciales de StDUsuarios
	        boolean credencialesValidas = stDUsuarios.verificarUsuario(nombreUsuario, contraseña);
	        
	        // Devolver el resultado de la verificación de credenciales
	        return credencialesValidas;
	    } catch (SQLException e) {
	        // Manejar el error de la base de datos
	        e.printStackTrace();
	        return false; // Las credenciales son inválidas debido a un error
	    }
	}

	
}
