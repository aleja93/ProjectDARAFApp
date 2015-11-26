/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.daraf.projectdarafapp.facade;

import com.daraf.projectdarafprotocol.DBClient;
import com.daraf.projectdarafprotocol.appdb.MensajeRQ;
import com.daraf.projectdarafprotocol.appdb.MensajeRS;
import com.daraf.projectdarafprotocol.appdb.consultas.ConsultaClienteRQ;
import com.daraf.projectdarafprotocol.appdb.consultas.ConsultaClienteRS;
import com.daraf.projectdarafprotocol.appdb.ingresos.IngresoClienteRQ;
import com.daraf.projectdarafprotocol.appdb.ingresos.IngresoClienteRS;
import com.daraf.projectdarafprotocol.appdb.seguridades.AutenticacionEmpresaRQ;
import com.daraf.projectdarafprotocol.appdb.seguridades.AutenticacionEmpresaRS;
import com.daraf.projectdarafprotocol.appdb.consultas.ConsultaProductoRQ;
import com.daraf.projectdarafprotocol.appdb.consultas.ConsultaProductoRS;
import com.daraf.projectdarafprotocol.model.Cliente;
import com.daraf.projectdarafprotocol.appdb.ingresos.IngresoFacturaRQ;
import com.daraf.projectdarafprotocol.appdb.ingresos.IngresoFacturaRS;
import com.daraf.projectdarafprotocol.model.Detalle;
import com.daraf.projectdarafprotocol.model.DetalleFacturaAppRQ;
import com.daraf.projectdarafprotocol.model.Empresa;
import com.daraf.projectdarafprotocol.model.Producto;
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

    public static Boolean insernewclient(String id, String nombre, String telefono, String direccion)
    {
        DBClient dbclient =new DBClient();
        MensajeRQ msj = new MensajeRQ("appserver",MensajeRQ.ID_MENSAJE_INGRESOCLIENTE);
        IngresoClienteRQ ing =new IngresoClienteRQ();
        ing.setCliente(new Cliente(id,nombre,telefono,direccion));
        msj.setCuerpo(ing);
        MensajeRS response = dbclient.sendRequest(msj);
        IngresoClienteRS ingrs = (IngresoClienteRS) response.getCuerpo();
        if (ingrs.getResultado().equals("1")) {
            return true;
        } else {
            return false;
        }
    }//ing.getIdentificacion(),ing.getNombre(),ing.getDireccion(),ing.getTelefono(),ing.getDetalles()

    public static String insertarNuevaFactura(String id_facura, String identificacion, String fecha, String total, String numeroDetalles, List<DetalleFacturaAppRQ> detalles) {
        DBClient dbclient = new DBClient();
        MensajeRQ msj = new MensajeRQ(NetUtil.getLocalIPAddress(), MensajeRQ.ID_MENSAJE_INGRESOFACTURA);
        IngresoFacturaRQ ing = new IngresoFacturaRQ();
        ing.setIdentificacionCliente(identificacion);
        ing.setFecha(fecha);
        ing.setTotal(total);
        ing.setIdFactura(id_facura);
        ing.setNumeroDetalles(numeroDetalles);

        Detalle detalle = null;
        List<Detalle> details = new ArrayList<>();
        for (int i = 0; i < detalles.size(); i++) {
            detalle = new Detalle();
            detalle.setIdFactura(id_facura);
            detalle.setCantidad(detalles.get(i).getCantidad());
            detalle.setIdProducto(detalles.get(i).getIdProducto());
            Producto p = getProducto(detalles.get(i).getIdProducto());
            if (p == null) {
                detalle.setNombreProducto("quemado");//tengo que esperar el medtodo para buscar el numero de ese producto cn esa celda
            } else {
                if (Integer.valueOf(p.getCantidad().trim()) < Integer.valueOf(detalles.get(i).getCantidad())) {
                    return "2";//el numero de productos que se desea facturar es mayor al stock
                }
                detalle.setNombreProducto(p.getNombre());//tengo que esperar el medtodo para buscar el numero de ese producto cn esa celda
            }

            details.add(detalle);
        }

        ing.setDetalles(details);
        ing.setTotal(total);
        ing.setNumeroDetalles(String.valueOf(details.size()));
        
        msj.setCuerpo(ing);
        MensajeRS response = dbclient.sendRequest(msj);
        IngresoFacturaRS ingrs = (IngresoFacturaRS) response.getCuerpo();
        if (ingrs != null) {
            return ingrs.getResultado();
        } else {
            return "5";//no se construyo el mensaje correctamente
        }
    }

    public static Cliente consultaCliente(String datos) {
        DBClient dbClient = new DBClient();
        MensajeRQ msj = new MensajeRQ("appserver", MensajeRQ.ID_MENSAJE_CONSULTACLIENTE);
        ConsultaClienteRQ con = new ConsultaClienteRQ();
        con.setIdentificacion(datos);
        msj.setCuerpo(con);

        MensajeRS response = dbClient.sendRequest(msj);
        ConsultaClienteRS cli = (ConsultaClienteRS) response.getCuerpo();
        if (cli.getResultado().equals("1")) {
            return cli.getCliente();
        }
        return null;
    }

    public static Producto getProducto(String idProducto) {
        DBClient dbClient = new DBClient();
        MensajeRQ msj = new MensajeRQ("appserver", MensajeRQ.ID_MENSAJE_CONSULTAPRODUCTO);
        ConsultaProductoRQ cprq = new ConsultaProductoRQ();
        cprq.setIdProducto(idProducto);
        msj.setCuerpo(cprq);

        MensajeRS response = dbClient.sendRequest(msj);

        ConsultaProductoRS cprs = (ConsultaProductoRS) response.getCuerpo();
        if (cprs.getResultado().equals("1")) {
            return cprs.getProducto();
        }
        return null;
    }
}
