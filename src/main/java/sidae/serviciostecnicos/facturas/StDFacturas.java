package sidae.serviciostecnicos.facturas;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import sidae.jpa.beans.facturas.BdDFacturas;
import sidae.logica.facturas.Logicafacturas;

public class StDFacturas {

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
	
	
	public List<BdDFacturas> filtro(BdDFacturas objeto) throws SQLException, ParseException {
		
		
		List<BdDFacturas> lista = new ArrayList<>();
		String sql = "SELECT *  FROM BD_D_FACTURAS fac inner join BD_T_TIPOIMPUESTO btt ON"
				+ "	fac.ID_TIPOIMPUESTO = btt.ID_TIPOIMPUESTO WHERE 1 = 1";
		if(objeto != null) {
		
			if (objeto.getCoFacturas() != null && !objeto.getCoFacturas().isEmpty()) {
				sql += " AND CO_FACTURAS LIKE '%" +objeto.getCoFacturas()+"%'";
			}
			if (objeto.getDsFacturas() != null && !objeto.getDsFacturas().isEmpty()) {
				sql += " AND DS_FACTURAS LIKE '%" +objeto.getDsFacturas()+"%'";
			}
			if (objeto.getFeFacturas() != null) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				String fechaF = formatter.format(objeto.getFeFacturas());
				sql += " AND FE_FACTURAS = '" +fechaF+"'";
			}
			if (objeto.getImFacturas() != null) {
				sql += " AND IM_FACTURAS >= " + objeto.getImFacturas();
			}
			if (objeto.getImFacturasHasta() != null) {
				sql += " AND IM_FACTURAS"+ " <= " + objeto.getImFacturasHasta();
			}
			if (objeto.getIdImpuesto() != null) {
				sql += " AND fac.ID_TIPOIMPUESTO = " + objeto.getIdImpuesto();
			}
			if (objeto.getImBaseImponible() != null) {
				sql += " AND fac.IM_BASEIMPONIBLE = " + objeto.getImBaseImponible();
			}
			if (objeto.getIdPagos() != null) {
				sql += " AND fac.ID_PAGOS = " + objeto.getIdPagos();
			}
		}
		
		System.out.println(sql);
		
		try(Connection conn = conectar();
			PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			//stmt.executeUpdate(sql)
			
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				BdDFacturas item = new BdDFacturas(rs);
				lista.add(item);
			}
		}
		
		return lista;
	}
	
	public List<BdDFacturas> buscarTodas(BdDFacturas objeto) throws SQLException, ParseException {
		
		List<BdDFacturas> lista = new ArrayList<>();
		String sql = "SELECT * FROM BD_D_FACTURAS";
		
		try(Connection conn = conectar();
			PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			//stmt.executeUpdate(sql)
			
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				BdDFacturas item = new BdDFacturas(rs);
				lista.add(item);
			}
		}
		
		return lista;
	}
	
	public BdDFacturas item(Integer idFacturas) throws SQLException, ParseException{
	    BdDFacturas item = null;
	    String sql = "SELECT fac.*, btt.CO_TIPOIMPUESTO FROM BD_D_FACTURAS fac "
	               + "INNER JOIN BD_T_TIPOIMPUESTO btt ON fac.ID_TIPOIMPUESTO = btt.ID_TIPOIMPUESTO "
	               + "WHERE ID_FACTURAS = ?";
	    
	    try(Connection conn = conectar();
	        PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, idFacturas);
	        ResultSet rs = stmt.executeQuery();
	        while(rs.next()) {
	            item = new BdDFacturas(rs);
	        }
	    }
	    
	    return item;
	}
	
	public List<BdDFacturas> listarFacturasSinId() throws SQLException, ParseException {
	    List<BdDFacturas> lista = new ArrayList<>();
	    String sql = "SELECT fac.*, btt.CO_TIPOIMPUESTO FROM BD_D_FACTURAS fac " +
	                 "INNER JOIN BD_T_TIPOIMPUESTO btt ON fac.ID_TIPOIMPUESTO = btt.ID_TIPOIMPUESTO " +
	                 "LEFT JOIN BD_D_PAGOS pago ON fac.ID_PAGOS = pago.ID_PAGOS " +
	                 "WHERE (fac.ID_PAGOS IS null OR fac.ID_PAGOS == 0)";
	    
	    try (Connection conn = conectar();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            BdDFacturas item = new BdDFacturas(rs);
	            lista.add(item);
	        }
	    }
	    
	    return lista;
	}
	
	public List<BdDFacturas> itemFacturaPago(Integer idPago) throws SQLException, ParseException {
		List<BdDFacturas> lista = new ArrayList<>();
	    String sql = "SELECT fac.*, btt.CO_TIPOIMPUESTO FROM BD_D_FACTURAS fac "
	               + "INNER JOIN BD_T_TIPOIMPUESTO btt ON fac.ID_TIPOIMPUESTO = btt.ID_TIPOIMPUESTO "
	               + "INNER JOIN BD_D_PAGOS pago ON fac.ID_PAGOS = pago.ID_PAGOS "
	               + "WHERE pago.ID_PAGOS = ?";

	    try(Connection conn = conectar();
	        PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, idPago);
	        ResultSet rs = stmt.executeQuery();
	        while(rs.next()) {
	            BdDFacturas item = new BdDFacturas(rs);
	            lista.add(item);
	        }
	    }

	    return lista;
	}
	
	public boolean isExisteCodigo(String coFacturas) throws SQLException, ParseException{
		boolean tengoCodigo = false; 
		BdDFacturas item = null;
		String sql = "SELECT fac.*, btt.CO_TIPOIMPUESTO FROM BD_D_FACTURAS fac "
				+ "INNER JOIN BD_T_TIPOIMPUESTO btt ON fac.ID_TIPOIMPUESTO = btt.ID_TIPOIMPUESTO "
				+ "WHERE fac.CO_FACTURAS LIKE '%"+coFacturas+"%'";
		
		try(Connection conn = conectar();
				PreparedStatement stmt = conn.prepareStatement(sql)) {
				
				//stmt.executeUpdate(sql)
				ResultSet rs = stmt.executeQuery();
				while(rs.next()) {
					item = new BdDFacturas(rs);
					 }}
		if(item != null && item.getCoFacturas().equals(coFacturas)) {
			tengoCodigo =  true;			
		}	 
		
		return tengoCodigo;
	}
	
	public void alta(BdDFacturas objeto) throws SQLException { 	  
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	    // Insertar factura en la base de datos
	    String sql = "INSERT INTO BD_D_FACTURAS (CO_FACTURAS, DS_FACTURAS, FE_FACTURAS,"
	    		 + " ID_TIPOIMPUESTO, IM_BASEIMPONIBLE, IM_FACTURAS, TSTBD, USUARIOBD, ID_PAGOS) VALUES('"
	            + objeto.getCoFacturas() + "', '"
	            + objeto.getDsFacturas() + "', '"
	            + formatter.format(objeto.getFeFacturas()) + "', "
	            + objeto.getIdImpuesto() + ", "
	            + objeto.getImBaseImponible() + ", "
	            + objeto.getImFacturas() + ", " 
	            + " datetime('now') , '"
	            + objeto.getUsuarioBd() + "', " 
	    		+ objeto.getIdPagos() + ")";

	    try(Connection conn = conectar();
	            PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.executeUpdate();
	    }
	}
				
	public void actualiza(BdDFacturas objeto) throws SQLException{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");	    
	    // Calcular el impuesto utilizando el ID del tipo de impuesto
	    
        String sql = "UPDATE BD_D_FACTURAS SET "
        		+ "CO_FACTURAS = " + "'" + objeto.getCoFacturas() + "'"
        		+ ", DS_FACTURAS = " +"'" + objeto.getDsFacturas() + "'"
        		+ ", FE_FACTURAS = " + "'" + formatter.format(objeto.getFeFacturas()) + "'"
        		+ ", IM_FACTURAS = " + "'" + objeto.getImFacturas() + "'"
        		+ ", TSTBD =   datetime('now') "
        		+ ", USUARIOBD = " + "'"+objeto.getUsuarioBd() + "'"
        		+ ", ID_TIPOIMPUESTO = " + "'" + objeto.getIdImpuesto()+ "'"
        		+ ", IM_BASEIMPONIBLE = " + "'" + objeto.getImBaseImponible() + "'"
        		+ ", ID_PAGOS = " + "'" + objeto.getIdPagos() + "'"
        		+ " WHERE ID_FACTURAS = " +objeto.getIdFacturas();
        		
        
        try (Connection conn = conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
        		stmt.executeUpdate();
        }
	}
	
	public void baja(BdDFacturas objeto) throws SQLException{
        String sql = "DELETE FROM BD_D_FACTURAS WHERE ID_FACTURAS = " +objeto.getIdFacturas();
        
        
        try (Connection conn = conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
        		stmt.executeUpdate();
        }
	}
	

	
	public static void main(String[] args) throws SQLException, ParseException {
		//Filtrar un usuario en concreto según su id
		Logicafacturas logica = new Logicafacturas();
		List<BdDFacturas> lista = logica.buscar(new BdDFacturas());
		System.out.println(lista);
				
		/*StDFacturas f = new StDFacturas();
		BdDFacturas factura = f.item(14); //El número de id concreto.	
	    System.out.println("Se ha obtenido el registro: " +factura.getCoFacturas());*/
	    
		/*BdDFacturas factura = new BdDFacturas();*/
		
	    //Dar de alta una factura por parametros concretos
		/*factura.setCoFacturas("FACT 0015");
		factura.setDsFacturas("Factura número 015");
		factura.setImFacturas(BigDecimal.valueOf(1500));
		String fecha = "2024-03-05";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha2 = formatter.parse(fecha);
        factura.setFeFacturas(fecha2);
		factura.setUsuarioBd("Edu"); 
	    logica.grabar(factura);
	    System.out.println("Proceso terminado.");*/
		
		//Actualizar los datos de un usuario
		/*factura.setCoFacturas("FACT 007");
	    factura.setDsFacturas("Factura número 007 (actualizado)");
		factura.setImFacturas(BigDecimal.valueOf(1200));
		String fecha = "2024-03-05";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha2 = formatter.parse(fecha);
		factura.setFeFacturas(fecha2);
		factura.setUsuarioBd("Laura");
		f.actualiza(factura);
	    System.out.println("Se ha actualizado el registro: " +factura.getCoFacturas());*/
		
		/*//Borrar facturas
		logica.baja(factura);
		System.out.println("Se ha borrado el registro: " +factura.getCoFacturas());*/
	}
	
}
