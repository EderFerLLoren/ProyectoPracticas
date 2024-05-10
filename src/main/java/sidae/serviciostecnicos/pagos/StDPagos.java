package sidae.serviciostecnicos.pagos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import sidae.jpa.beans.pagos.BdDPagos;



public class StDPagos {
	
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
		
		
		public List<BdDPagos> filtro(BdDPagos objeto) throws SQLException, ParseException {
			
			List<BdDPagos> lista = new ArrayList<>();
			String sql = "SELECT * FROM BD_D_PAGOS WHERE 1 = 1";
			
			if (objeto.getCoPagos() != null && !objeto.getCoPagos().isEmpty()) {
				sql += " AND CO_PAGOS LIKE '%" +objeto.getCoPagos()+"%'";
			}
			if (objeto.getDsPagos() != null && !objeto.getDsPagos().isEmpty()) {
				sql += " AND DS_PAGOS LIKE '%" +objeto.getDsPagos()+"%'";
			}
			if (objeto.getDsTercero() != null && !objeto.getDsTercero().isEmpty()) {
				sql += " AND DS_TERCERO LIKE '%" +objeto.getDsTercero()+"%'";
			}
			if (objeto.getImPagos() != null) {
				sql += " AND IM_PAGOS >= " + objeto.getImPagos();
			}
			if (objeto.getImPagosHasta() != null) {
				sql += " AND IM_PAGOS"+ " <= " + objeto.getImPagosHasta();
			}
			
			System.out.println(sql);
			
			try(Connection conn = conectar();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
				
				//stmt.executeUpdate(sql)
				
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					BdDPagos item = new BdDPagos(rs);
					lista.add(item);
				}
			}
			
			return lista;
		}
		
		
		
		public List<BdDPagos> buscarTodos(BdDPagos objeto) throws SQLException, ParseException {
			
			List<BdDPagos> lista = new ArrayList<>();
			String sql = "SELECT * FROM BD_D_PAGOS";
			
			try(Connection conn = conectar();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
				
				//stmt.executeUpdate(sql)
				
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					BdDPagos item = new BdDPagos(rs);
					lista.add(item);
				}
			}
			
			return lista;
		}
		
		public BdDPagos item(Integer idPagos) throws SQLException, ParseException{
			BdDPagos item = null;
		    String sql = "SELECT * "
		    		+ "FROM BD_D_PAGOS "
		            + "WHERE ID_PAGOS = ?";
		    
		    try(Connection conn = conectar();
		        PreparedStatement stmt = conn.prepareStatement(sql)) {
		        stmt.setInt(1, idPagos);
		        ResultSet rs = stmt.executeQuery();
		        while(rs.next()) {
		            item = new BdDPagos(rs);
		        }
		    }
		    
		    return item;
		}

		public boolean isExisteCodigo(String coPagos) throws SQLException, ParseException{
			boolean tengoCodigo = false; 
			BdDPagos item = null;
			String sql = "SELECT * FROM BD_D_PAGOS WHERE CO_PAGOS LIKE '%"+coPagos+"%'";
			
			try(Connection conn = conectar();
					PreparedStatement stmt = conn.prepareStatement(sql)) {
					
					//stmt.executeUpdate(sql)
					ResultSet rs = stmt.executeQuery();
					while(rs.next()) {
						item = new BdDPagos(rs);
						 }}
			if(item != null && item.getCoPagos().equals(coPagos)) {
				tengoCodigo =  true;			
			}	 
			
			return tengoCodigo;
		}
		
		public void alta(BdDPagos pago) throws SQLException { 	  
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		    // Insertar factura en la base de datos
		    String sql = "INSERT INTO BD_D_PAGOS (CO_PAGOS, DS_PAGOS, FE_PAGOS,"
		    		 + " IM_PAGOS, DS_TERCERO, DS_CUENTA,TSTBD, USUARIOBD) VALUES('"
		            + pago.getCoPagos() + "', '"
		            + pago.getDsPagos() + "', '"
		            + formatter.format(pago.getFePagos()) + "', "
		            + pago.getImPagos() + ", '" 
		            + pago.getDsTercero() + "', '"
				    + pago.getDsCuenta() + "', "
		            + " datetime('now') , '"
		            + pago.getUsuarioBd()+ "')" ;

		    try(Connection conn = conectar();
		            PreparedStatement stmt = conn.prepareStatement(sql)) {
		            stmt.executeUpdate();
		    }
		}
			
			
		public void actualiza(BdDPagos objeto) throws SQLException{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");	    
		    // Calcular el impuesto utilizando el ID del tipo de impuesto
		    
	        String sql = "UPDATE BD_D_PAGOS SET "
	        		+ "CO_PAGOS = " + "'" + objeto.getCoPagos() + "'"
	        		+ ", DS_PAGOS = " +"'" + objeto.getDsPagos() + "'"
	        		+ ", FE_PAGOS = " + "'" + formatter.format(objeto.getFePagos()) + "'"
	        		+ ", IM_PAGOS = " + "'" + objeto.getImPagos() + "'"
	        		+ ", DS_TERCERO = " + "'"+objeto.getDsTercero() + "'"
	        		+ ", DS_CUENTA = " + "'" + objeto.getDsCuenta()+ "'"
	        		+ ", TSTBD =   datetime('now') "
	        		+ ", USUARIOBD = " + "'" + objeto.getUsuarioBd() + "'"
	        		+ " WHERE ID_PAGOS = " +objeto.getIdPagos();
	        			        
	        try (Connection conn = conectar();
	                PreparedStatement stmt = conn.prepareStatement(sql)) {
	        		stmt.executeUpdate();
	        }
		}
		
		public void baja(BdDPagos objeto) throws SQLException{
	        String sql = "DELETE FROM BD_D_PAGOS WHERE ID_PAGOS = " +objeto.getIdPagos();
	        
	        
	        try (Connection conn = conectar();
	                PreparedStatement stmt = conn.prepareStatement(sql)) {
	        		stmt.executeUpdate();
	        }
		}
		
}
