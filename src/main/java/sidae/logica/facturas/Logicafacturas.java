package sidae.logica.facturas;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import sidae.jpa.beans.facturas.BdDFacturas;
import sidae.jpa.beans.facturas.BdDImpuestos;
import sidae.serviciostecnicos.facturas.StDFacturas;
import sidae.serviciostecnicos.facturas.StDImpuestos;

public class Logicafacturas {
	
private StDFacturas stFactura = new StDFacturas(); 

	public List<BdDFacturas> buscar(BdDFacturas objeto) throws SQLException, ParseException {
		return stFactura.filtro(objeto);
	}

	public BdDFacturas item(Integer idFacturas) throws SQLException, ParseException {		
		BdDFacturas item = stFactura.item(idFacturas);
		return item;		
	}
	
	private void alta(BdDFacturas factura) throws SQLException, ParseException { 
		if(factura != null && stFactura.isExisteCodigo(factura.getCoFacturas())) {
			System.out.println("Ya existe ese código");			
		}else {			
			stFactura.alta(factura);			
		}				
	} 
	
	public void grabar(BdDFacturas objeto) throws SQLException, ParseException {
		// Calcular el impuesto utilizando el ID del tipo de impuesto	 
		BigDecimal imBaseimponible = BigDecimal.ZERO;
	    if(objeto.getIdImpuesto() != null ) { 		    
		    
	    	imBaseimponible = calcularBaseImponible(objeto.getIdImpuesto(), objeto.getImFacturas());
	    	objeto.setImBaseImponible(imBaseimponible);
	    }
		
		
	    if (objeto.getIdFacturas() == null && !stFactura.isExisteCodigo(objeto.getCoFacturas())) { 
	    	// Factura nueva: ID es nulo	
	        System.out.println("Damos de alta");
	        this.alta(objeto);
	    }else {
	        System.out.println("Actualizo");
	        stFactura.actualiza(objeto);
	    }
	}
	
	public void baja(BdDFacturas objeto) throws SQLException{
		if(objeto == null) {
			System.out.println("No hay factura para borrar");			
		}else {			
			stFactura.baja(objeto);			
		}
	}
	
	
	private BigDecimal calcularPorcentaje(BigDecimal porcentaje) {			
	    return porcentaje.divide(BigDecimal.valueOf(100));
	}
    
	public BigDecimal calcularImpuesto(int idImpuesto, BigDecimal importe) throws SQLException, ParseException {
	    BigDecimal impuesto = BigDecimal.ZERO;
	    StDImpuestos stImpuesto = new StDImpuestos();
	    BdDImpuestos bdImpuesto = stImpuesto.item(idImpuesto);
	    
	    if (bdImpuesto != null) {	        
	        BigDecimal porcentajeDecimal = calcularPorcentaje(bdImpuesto.getPjImpuesto());
	        impuesto = importe.multiply(porcentajeDecimal);
	        
	    } else {
	        System.out.println("El impuesto con el ID " + idImpuesto + " no fue encontrado.");
	    }
	    
	    return impuesto;
	}
	
	public BigDecimal calcularBaseImponible(int idImpuesto, BigDecimal importe) throws SQLException, ParseException {
	    BigDecimal impuesto = calcularImpuesto(idImpuesto, importe);
	    // Sumar el impuesto al importe para obtener la base imponible
	    BigDecimal baseImponible = importe.add(impuesto);
	    return baseImponible;
	}
	
}	

