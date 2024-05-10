package sidae.serviciostecnicos.usuarios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import sidae.jpa.beans.usuarios.BdDUsuarios;

public class StDUsuarios {
	private final String CADENA_CONEXION = "jdbc:sqlite:C:\\SVN\\Practicas_Facturas\\datos\\database.db";

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private Connection conectar() throws SQLException {
		return DriverManager.getConnection(CADENA_CONEXION);
	}
	
	public List<BdDUsuarios> filtro(BdDUsuarios objeto) throws SQLException, ParseException {
		List<BdDUsuarios> lista = new ArrayList<>();
		String sql = "SELECT *  FROM BD_D_USUARIOS WHERE 1 = 1";
		if (objeto != null) {

			if (objeto.getCoUsuarios() != null && !objeto.getCoUsuarios().isEmpty()) {
				sql += " AND CO_USUARIOS LIKE '%" + objeto.getCoUsuarios() + "%'";
			}
			if (objeto.getDsUsuarios() != null && !objeto.getDsUsuarios().isEmpty()) {
				sql += " AND DS_USUARIOS LIKE '%" + objeto.getDsUsuarios() + "%'";
			}
			if (objeto.getDsPassword() != null && !objeto.getDsPassword().isEmpty()) {
				sql += " AND DS_PASSWORD LIKE '%" + objeto.getDsPassword() + "%'";
			}
			if (objeto.getDsEmail() != null && !objeto.getDsEmail().isEmpty()) {
				sql += " AND DS_EMAIL LIKE '%" + objeto.getDsEmail() + "%'";
			}
			if (objeto.getBoAdmin() != null) {
				sql += " AND BO_ADMIN LIKE '%" + objeto.getBoAdmin() + "%'";
			}
			
			
		}
		System.out.println(sql);

		try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				BdDUsuarios item = new BdDUsuarios(rs);
				lista.add(item);
			}
		}

		return lista;
	}
	
	public BdDUsuarios item(String coUsuario) throws SQLException, ParseException {
		BdDUsuarios item = null;
	    String sql = "SELECT * FROM BD_D_USUARIOS WHERE CO_USUARIOS = ?";
	    
	    try (Connection conn = conectar();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, coUsuario);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            item = new BdDUsuarios(rs);
	        }
	    }
	    
	    return item;
	}
	
	public List<BdDUsuarios> buscarTodosUsuarios() throws SQLException, ParseException {

		List<BdDUsuarios> lista = new ArrayList<>();
		String sql = "SELECT * FROM BD_D_USUARIOS";

		try (Connection conn = conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			// stmt.executeUpdate(sql)

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				BdDUsuarios item = new BdDUsuarios(rs);
				lista.add(item);
			}
		}

		return lista;
	}
	
	public void alta(BdDUsuarios objeto) throws SQLException { 	  
	    String sql = "INSERT INTO BD_D_USUARIOS (CO_USUARIOS, DS_USUARIOS, DS_PASSWORD,"
	    		 + " DS_EMAIL, BO_ADMIN, USUARIOBD, TSTBD) VALUES('"
	            + objeto.getCoUsuarios() + "', '"
	            + objeto.getDsUsuarios() + "', '"
	            + objeto.getDsPassword() + "', '"
	            + objeto.getDsEmail() + "', "	            
	            + objeto.getBoAdmin() + ", '"
	            + objeto.getUsuarioBd() + "', "
	            + " datetime('now')" + ")";

	    try(Connection conn = conectar();
	            PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.executeUpdate();
	    }
	}

	public void actualiza(BdDUsuarios objeto) throws SQLException{
        String sql = "UPDATE BD_D_USUARIOS  SET "
        		+ "CO_USUARIOS = " + "'" + objeto.getCoUsuarios() + "'"
        		+ ", DS_USUARIOS = " +"'" + objeto.getDsUsuarios() + "'"
        		+ ", DS_PASSWORD = " + "'" + objeto.getDsPassword() + "'"
        		+ ", DS_EMAIL = " + "'" + objeto.getDsEmail() + "'"
        		+ ", BO_ADMIN = " + "'" + objeto.getBoAdmin()+ "'"
        		+ ", USUARIOBD = " + "'"+objeto.getUsuarioBd() + "'"
        		+ ", TSTBD =   datetime('now') "
        		+ " WHERE ID_USUARIOS = " +objeto.getIdUsuarios();
        		
        
        try (Connection conn = conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
        		stmt.executeUpdate();
        }
	}
	
	public void baja(BdDUsuarios objeto) throws SQLException{
        String sql = "DELETE FROM BD_D_USUARIOS WHERE ID_USUARIOS = " +objeto.getIdUsuarios();
        
        
        try (Connection conn = conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
        		stmt.executeUpdate();
        }
	}
	
	public boolean isExisteCodigo(String coUsuario) throws SQLException, ParseException{
		boolean tengoCodigo = false; 
		BdDUsuarios item = null;
		String sql = "SELECT * FROM BD_D_USUARIOS WHERE CO_USUARIOS LIKE '%"+coUsuario+"%'";
		
		try(Connection conn = conectar();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
				
				//stmt.executeUpdate(sql)
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					item = new BdDUsuarios(rs);
					 }}
		if(item != null && item.getCoUsuarios().equals(coUsuario)) {
			tengoCodigo =  true;			
		}	 
		
		return tengoCodigo;
	}
	
	public boolean verificarUsuario(String coUsuario, String contraseña) throws SQLException {
	    boolean credencialesValidas = false;
	    String sql = "SELECT * FROM BD_D_USUARIOS WHERE CO_USUARIOS = ? AND DS_PASSWORD = ?";
	    
	    try (Connection conn = conectar();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, coUsuario);
	        stmt.setString(2, contraseña);
	        
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) {
	            credencialesValidas = true; // Se encontró un usuario con las credenciales proporcionadas
	        }
	    }
	    
	    return credencialesValidas;
	}
	
}
