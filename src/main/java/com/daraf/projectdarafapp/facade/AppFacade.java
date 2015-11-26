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
import com.daraf.projectdarafprotocol.model.Cliente;
import com.daraf.projectdarafprotocol.model.Empresa;

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

    public static Boolean insernewclient(String id, String nombre, String direccion, String telefono)
    {
        DBClient dbclient =new DBClient();
        MensajeRQ msj = new MensajeRQ("appserver",MensajeRQ.ID_MENSAJE_INGRESOCLIENTE);
        IngresoClienteRQ ing =new IngresoClienteRQ();
        ing.setId(id);
        ing.setNombre(nombre);
        ing.setDireccion(direccion);
        ing.setTelefono(telefono);
        msj.setCuerpo(ing);
        MensajeRS response = dbclient.sendRequest(msj);
        IngresoClienteRS ingrs=(IngresoClienteRS) response.getCuerpo();
        if(ingrs.getResultado().equals("1")){
            return true;
        }
        else{
            return false;
        }
    }
    public static Cliente consultaCliente(String datos)
    {
        DBClient dbClient=new DBClient();
        MensajeRQ msj=new MensajeRQ("appserver",MensajeRQ.ID_MENSAJE_CONSULTACLIENTE);
        ConsultaClienteRQ con=new ConsultaClienteRQ();
        con.setIdentificacion(datos);
        msj.setCuerpo(con);
        
        MensajeRS response=dbClient.sendRequest(msj);
        ConsultaClienteRS cli =(ConsultaClienteRS) response.getCuerpo();
         if (cli.getResultado().equals("1")) {
            if (cli.getCliente().getIdentificacion().equals(datos)) {
                return cli.getCliente();
            }
        }
        return null;
    }
}
