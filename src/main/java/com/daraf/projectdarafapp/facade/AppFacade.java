/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.daraf.projectdarafapp.facade;

import com.daraf.projectdarafprotocol.DBClient;
import com.daraf.projectdarafprotocol.appdb.MensajeRQ;
import com.daraf.projectdarafprotocol.appdb.MensajeRS;
import com.daraf.projectdarafprotocol.appdb.seguridades.AutenticacionEmpresaRQ;
import com.daraf.projectdarafprotocol.appdb.seguridades.AutenticacionEmpresaRS;
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
}
