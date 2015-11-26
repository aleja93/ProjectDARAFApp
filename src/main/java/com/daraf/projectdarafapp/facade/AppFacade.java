/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.daraf.projectdarafapp.facade;

import com.daraf.projectdarafprotocol.DBClient;
import com.daraf.projectdarafprotocol.appdb.MensajeRQ;
import com.daraf.projectdarafprotocol.appdb.MensajeRS;
import com.daraf.projectdarafprotocol.appdb.ingresos.IngresoClienteRQ;
import com.daraf.projectdarafprotocol.appdb.ingresos.IngresoClienteRS;
import com.daraf.projectdarafprotocol.appdb.seguridades.AutenticacionEmpresaRQ;
import com.daraf.projectdarafprotocol.appdb.seguridades.AutenticacionEmpresaRS;
import com.daraf.projectdarafprotocol.appdb.ingresos.IngresoFacturaRQ;
import com.daraf.projectdarafprotocol.appdb.ingresos.IngresoFacturaRS;
import com.daraf.projectdarafprotocol.model.Detalle;
import com.daraf.projectdarafprotocol.model.DetalleFacturaAppRQ;
import com.daraf.projectdarafprotocol.model.Empresa;
import com.daraf.projectdarafutil.NetUtil;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dennys
 */
public class AppFacade {

    public static Empresa getAuthenticationEmpresa(String user, String password) {
        DBClient dbClient = new DBClient();
        MensajeRQ msj = new MensajeRQ("appserver", MensajeRQ.ID_MENSAJE_AUTENTICACIONCLIENTE);
        AutenticacionEmpresaRQ aut = new AutenticacionEmpresaRQ();
        aut.setIdentificacion(user);
        msj.setCuerpo(aut);

        MensajeRS response = dbClient.sendRequest(msj);

        AutenticacionEmpresaRS autRS = (AutenticacionEmpresaRS) response.getCuerpo();
        if (autRS.getResultado().equals("1")) {
            if (autRS.getEmpresa().getPassword().trim().equals(password.trim())) {
                return autRS.getEmpresa();
            }
        }
        return null;
    }

    public static Boolean insernewclient(String id, String nombre, String direccion, String telefono) {
        DBClient dbclient = new DBClient();
        MensajeRQ msj = new MensajeRQ("appserver", MensajeRQ.ID_MENSAJE_INGRESOCLIENTE);
        IngresoClienteRQ ing = new IngresoClienteRQ();
        ing.setId(id);
        ing.setNombre(nombre);
        ing.setDireccion(direccion);
        ing.setTelefono(telefono);
        msj.setCuerpo(ing);
        MensajeRS response = dbclient.sendRequest(msj);
        IngresoClienteRS ingrs = (IngresoClienteRS) response.getCuerpo();
        if (ingrs.getResultado().equals("1")) {
            return true;
        } else {
            return false;
        }
    }//ing.getIdentificacion(),ing.getNombre(),ing.getDireccion(),ing.getTelefono(),ing.getDetalles()

    public static String insertarNuevaFactura(String id_facura, String identificacion, String fecha, String total, List<DetalleFacturaAppRQ> detalles) {
        DBClient dbclient = new DBClient();
        MensajeRQ msj = new MensajeRQ(NetUtil.getLocalIPAddress(), MensajeRQ.ID_MENSAJE_INGRESOFACTURA);
        IngresoFacturaRQ ing = new IngresoFacturaRQ();
        ing.setIdentificacionCliente(identificacion);
        ing.setFecha(fecha);

        ing.setIdFactura(id_facura);
        //ing.setDetalles(detalles);
        Detalle detalle = null;
        List<Detalle> details = new ArrayList<>();
        for (int i = 0; i < detalles.size(); i++) {
            detalle = new Detalle();
            detalle.setIdFactura(id_facura);
            detalle.setCantidad(detalles.get(i).getCantidad());
            detalle.setIdProducto(detalles.get(i).getIdProducto());

            detalle.setNombreProducto("quemado");//tengo que esperar el medtodo para buscar el numero de ese producto cn esa celda
            details.add(detalle);
        }
        ing.setDetalles(details);
        ing.setTotal(total);
        ing.setNumeroDetalles(String.valueOf(detalles.size()));

        MensajeRS response = dbclient.sendRequest(msj);
        IngresoFacturaRS ingrs = (IngresoFacturaRS) response.getCuerpo();
        if (ingrs != null) {
            return ingrs.getResultado();
        } else {
            return "4";
        }
    }
}
