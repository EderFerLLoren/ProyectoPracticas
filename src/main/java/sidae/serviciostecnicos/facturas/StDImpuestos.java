package sidae.serviciostecnicos.facturas;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import sidae.jpa.beans.facturas.BdDImpuestos;


public class StDImpuestos {

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
	
	public BdDImpuestos item(Integer idImpuesto) throws SQLException, ParseException {
		BdDImpuestos item = null;
	    String sql = "SELECT * FROM BD_T_TIPOIMPUESTO WHERE ID_TIPOIMPUESTO = ?";
	    
	    try (Connection conn = conectar();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, idImpuesto);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            item = new BdDImpuestos(rs);
	        }
	    }
	    
	    return item;
	}
	
	public List<BdDImpuestos> obtenerTiposImpuesto() throws SQLException {
        List<BdDImpuestos> tiposImpuesto = new ArrayList<>();
        String sql = "SELECT ID_TIPOIMPUESTO, CO_TIPOIMPUESTO, DS_TIPOIMPUESTO, PJ_TIPOIMPUESTO, TSTBD, USUARIOBD FROM BD_T_TIPOIMPUESTO";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ID_TIPOIMPUESTO");
                String codigo = rs.getString("CO_TIPOIMPUESTO");
                String descripcion = rs.getString("DS_TIPOIMPUESTO");
                BigDecimal porcentaje = rs.getBigDecimal("PJ_TIPOIMPUESTO");
                String tstbd = rs.getString("TSTBD");
                String usuarioBd = rs.getString("USUARIOBD");

                BdDImpuestos impuesto = new BdDImpuestos(id, codigo, descripcion, porcentaje, tstbd, usuarioBd);
                tiposImpuesto.add(impuesto);
            }
        }

        return tiposImpuesto;
    }


	
}
