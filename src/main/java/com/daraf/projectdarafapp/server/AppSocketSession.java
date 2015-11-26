/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.daraf.projectdarafapp.server;

/**
 *
 * @author RAUL
 */
import com.daraf.projectdarafapp.facade.AppFacade;
import com.daraf.projectdarafprotocol.Mensaje;
import com.daraf.projectdarafprotocol.clienteapp.seguridades.AutenticacionEmpresaRQ;
import com.daraf.projectdarafprotocol.clienteapp.MensajeRQ;
import com.daraf.projectdarafprotocol.clienteapp.MensajeRS;
import com.daraf.projectdarafprotocol.clienteapp.consultas.ConsultaClienteRQ;
import com.daraf.projectdarafprotocol.clienteapp.consultas.ConsultaClienteRS;
import com.daraf.projectdarafprotocol.clienteapp.consultas.ConsultaProductoRQ;
import com.daraf.projectdarafprotocol.clienteapp.consultas.ConsultaProductoRS;
import com.daraf.projectdarafprotocol.clienteapp.ingresos.IngresoClienteRQ;
import com.daraf.projectdarafprotocol.clienteapp.ingresos.IngresoClienteRS;
import com.daraf.projectdarafprotocol.clienteapp.ingresos.IngresoFacturaRQ;
import com.daraf.projectdarafprotocol.clienteapp.ingresos.IngresoFacturaRS;
import com.daraf.projectdarafprotocol.clienteapp.seguridades.AutenticacionEmpresaRS;
import com.daraf.projectdarafprotocol.model.Cliente;
import com.daraf.projectdarafprotocol.model.Empresa;
import com.daraf.projectdarafprotocol.model.Producto;
import com.daraf.projectdarafutil.NetUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AppSocketSession extends Thread {

    private static Integer global = 0;
    private PrintWriter output;
    private BufferedReader input;
    private Socket socket;

    private Integer id;

    public AppSocketSession(Socket socket) throws IOException {

        this.id = AppSocketSession.global++;
        this.socket = socket;
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        output = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {

            String userInput;

            while ((userInput = input.readLine()) != null) {

                if ("FIN".equalsIgnoreCase(userInput)) {
                    break;
                }
                System.out.println("Hilo: " + this.id + " Mensaje recibido: " + userInput);
                MensajeRQ msj = new MensajeRQ();
                if (msj.build(userInput)) {
                    if (msj.getCabecera().getIdMensaje().equals(Mensaje.ID_MENSAJE_AUTENTICACIONCLIENTE)) {

                        //metodo de autenticacion
                        AutenticacionEmpresaRQ aut = (AutenticacionEmpresaRQ) msj.getCuerpo();
                        Empresa response = AppFacade.getAuthenticationEmpresa(aut.getUserId(), aut.getPassword());

                        MensajeRS mensajeRS = new MensajeRS("appserver", Mensaje.ID_MENSAJE_AUTENTICACIONCLIENTE);
                        AutenticacionEmpresaRS autRS = new AutenticacionEmpresaRS();
                        if (response != null) {
                            autRS.setResultado("1");
                            autRS.setEmpresa(response);
                        } else {
                            autRS.setResultado("2");
                        }

                        mensajeRS.setCuerpo(autRS);
                        output.write(mensajeRS.asTexto() + "\n");
                        output.flush();
                    }
                    if (msj.getCabecera().getIdMensaje().equals(Mensaje.ID_MENSAJE_INGRESOCLIENTE)) {
                        IngresoClienteRQ ing = (IngresoClienteRQ) msj.getCuerpo();
                
                        Boolean ingresocorrecto = AppFacade.insernewclient(ing.getCliente().getIdentificacion(), ing.getCliente().getNombre(), ing.getCliente().getTelefono(), ing.getCliente().getDireccion());
                       // System.out.print("******"+ing.getCliente().getIdentificacion());
                        MensajeRS mensajeRS = new MensajeRS("appserver", Mensaje.ID_MENSAJE_INGRESOCLIENTE);
                        IngresoClienteRS ingrs = new IngresoClienteRS();
                        if (ingresocorrecto) {
                            ingrs.setResultado("1");
                        } else {
                            ingrs.setResultado("2");
                        }
                        mensajeRS.setCuerpo(ingrs);
                        output.write(mensajeRS.asTexto() + "\n");
                        output.flush();
                    }
                    if (msj.getCabecera().getIdMensaje().equals(Mensaje.ID_MENSAJE_CONSULTACLIENTE)) {
                        ConsultaClienteRQ ing = (ConsultaClienteRQ) msj.getCuerpo();
                        Cliente response = AppFacade.consultaCliente(ing.getIdentificacion());
                        MensajeRS mensajeRS = new MensajeRS("appserver", Mensaje.ID_MENSAJE_CONSULTACLIENTE);
                        ConsultaClienteRS cli = new ConsultaClienteRS();
                        if (response != null) {
                            cli.setResultado("1");
                            cli.setCliente(response);
                        } else {
                            cli.setResultado("2");
                        }
                        mensajeRS.setCuerpo(cli);
                        output.write(mensajeRS.asTexto() + "\n");
                        output.flush();
                    }
                    if (msj.getCabecera().getIdMensaje().equals(Mensaje.ID_MENSAJE_CONSULTAPRODUCTO)) {

                        //metodo de consulta producto
                        ConsultaProductoRQ cprq = (ConsultaProductoRQ) msj.getCuerpo();
                        Producto response = AppFacade.getProducto(cprq.getIdProducto());

                        MensajeRS mensajeRS = new MensajeRS("appserver", Mensaje.ID_MENSAJE_CONSULTAPRODUCTO);
                        ConsultaProductoRS cprs = new ConsultaProductoRS();
                        if (response != null) {
                            cprs.setResultado("1");
                            cprs.setProducto(response);
                        } else {
                            cprs.setResultado("2");
                        }

                        mensajeRS.setCuerpo(cprs);
                        output.write(mensajeRS.asTexto() + "\n");
                        output.flush();
                    }
                    if (msj.getCabecera().getIdMensaje().equals(Mensaje.ID_MENSAJE_INGRESOFACTURA)) {                        
                        IngresoFacturaRQ ing = (IngresoFacturaRQ) msj.getCuerpo();
                        String ingreso = AppFacade.insertarNuevaFactura(ing.getIdFactura(), ing.getIdentificacion(), ing.getFecha(), ing.getTotal(), ing.getNumeroDetalles(), ing.getDetalles());
                        MensajeRS mensajeRS = new MensajeRS(NetUtil.getLocalIPAddress(), Mensaje.ID_MENSAJE_INGRESOFACTURA);
                        IngresoFacturaRS ingrs = new IngresoFacturaRS();
                        ingrs.setResultado(ingreso);
                        mensajeRS.setCuerpo(ingrs);
                        output.write(mensajeRS.asTexto() + "\n");
                        output.flush();
                    }
                } else {
                    output.write(Mensaje.ID_MENSAJE_FALLOBUILD + "\n");
                    output.flush();
                }

            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e);
        }
    }

}
