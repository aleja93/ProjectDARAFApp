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
import com.daraf.projectdarafprotocol.clienteapp.MensajeRQ;
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
                System.out.println("Hilo: " + this.id + " Mensaje recibido: " + userInput);
                MensajeRQ msj = new MensajeRQ();
                if (msj.build(userInput)) {
                    if (msj.getCabecera().getIdMensaje().equals("Autenticacion")) {
                    //metodo de autenticacion
                        
                    
                    }
                }

                output.flush();
                if ("FIN".equalsIgnoreCase(userInput)) {
                    break;
                }

            }
            socket.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}