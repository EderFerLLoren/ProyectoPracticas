package sidae.logica.pagos;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sidae.jpa.beans.facturas.BdDFacturas;
import sidae.jpa.beans.pagos.BdDPagos;
import sidae.serviciostecnicos.pagos.StDPagos;
import sidae.serviciostecnicos.facturas.StDFacturas;

public class Logicapagos {

	private StDPagos stPago = new StDPagos();
	private StDFacturas stDFacturas = new StDFacturas();
	
	public List<BdDPagos> buscar(BdDPagos objeto) throws SQLException, ParseException {
		return stPago.filtro(objeto);
	}
	
	public BdDPagos item(Integer idPagos) throws SQLException, ParseException {		
		BdDPagos item = stPago.item(idPagos);
		return item;		
	}
	
	private void alta(BdDPagos pago) throws SQLException, ParseException { 
		if(pago != null && stPago.isExisteCodigo(pago.getCoPagos())) {
			System.out.println("Ya existe ese código");			
		}else {			
			stPago.alta(pago);			
		}
		
		
	}
	
	public void grabar(BdDPagos objeto) throws SQLException, ParseException {

	    if (objeto.getIdPagos() == null && !stPago.isExisteCodigo(objeto.getCoPagos())) { 
	    	// Factura nueva: ID es nulo	
	        System.out.println("Damos de alta");
	        this.alta(objeto);
	    }else {
	        System.out.println("Actualizo");
	        stPago.actualiza(objeto);
	    }
	    
	}
	
	public boolean verificarExistenciaCodigo(BdDPagos objeto) throws SQLException, ParseException {
	    return objeto.getIdPagos() == null && stPago.isExisteCodigo(objeto.getCoPagos());
	}
	
	public void baja(BdDPagos objeto) throws SQLException{
		if(objeto == null) {
			System.out.println("No hay PAGO para borrar");			
		}else {			
			stPago.baja(objeto);			
		}
	}
	
	
	   public boolean validarCuentaIBAN(String cuenta) {
	       // Patrón de cuenta bancaria IBAN
	       String patron = "ES\\d{22}";
	        
	       Pattern pattern = Pattern.compile(patron);
	       Matcher matcher = pattern.matcher(cuenta);
	        
	       return matcher.matches();
	   }
	
	   public List<BdDFacturas> listarTodasFacturas() throws SQLException, ParseException {
			return stDFacturas.buscarTodas(new BdDFacturas());
	    }
	   
	   public List<BdDFacturas> listarFacturasSinPagos() throws SQLException, ParseException {
		    BdDFacturas filtro = new BdDFacturas();
		    filtro.setIdPagos(null); // Establecer el idPagos como nulo para buscar facturas sin pagos
		    return stDFacturas.buscarTodas(filtro);
		}
	   
	  
}
