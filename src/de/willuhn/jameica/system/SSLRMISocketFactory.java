/**********************************************************************
 * $Source: /cvsroot/jameica/jameica/src/de/willuhn/jameica/system/Attic/SSLRMISocketFactory.java,v $
 * $Revision: 1.7 $
 * $Date: 2005/01/12 11:32:43 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.system;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import de.willuhn.logging.Logger;

public class SSLRMISocketFactory extends RMISocketFactory {

	private SSLServerSocketFactory serverSocketFactory;
  private SSLSocketFactory socketFactory;

  private boolean clientAuth = false;

  /**
   * ct.
   * @throws Exception
   */
  public SSLRMISocketFactory() throws Exception
  {
  	super();
  	Logger.info("init \"rmi over ssl\" socket factory");

    RMISocketFactory.setFailureHandler(new SSLRMIFailureHandler());

  	SSLContext context 	= Application.getSSLFactory().getSSLContext();
    serverSocketFactory = context.getServerSocketFactory();
    socketFactory 			= context.getSocketFactory();
  }

  /**
   * @see java.rmi.server.RMIClientSocketFactory#createSocket(java.lang.String, int)
   */
  public Socket createSocket(String host, int port) throws IOException
  {
		Logger.debug("Creating client socket to " + host + ":" + port);
    SSLSocket socket = (SSLSocket) socketFactory.createSocket(host, port);
		log(socket);
    return socket;
  }

  /**
   * @see java.rmi.server.RMIServerSocketFactory#createServerSocket(int)
   */
  public ServerSocket createServerSocket(int port) throws IOException
  {
		Logger.info("Creating server socket at port " + port + "/tcp");
    SSLServerSocket socket = (SSLServerSocket) serverSocketFactory.createServerSocket(port);
		log(socket);
    socket.setNeedClientAuth(clientAuth);
    socket.setWantClientAuth(clientAuth);
    return socket;
  }

	/**
   * Loggt die vom Socket unterstuetzten Protokolle und Cipher-Suites.
   * @param socket zu loggender Socket.
   */
  private void log(Object socket) throws IOException
	{
    String[] protos;
    String[] cipher;
    String socketType =  "CLIENT";
    if (socket instanceof SSLServerSocket)
    {
      socketType = "SERVER";
      SSLServerSocket s = (SSLServerSocket) socket;
      protos = s.getEnabledProtocols();
      cipher = s.getEnabledCipherSuites();
      Logger.info(socketType + " Socket receive buffer size: " + s.getReceiveBufferSize());
    }
    else
    {
      SSLSocket s = (SSLSocket) socket;
      protos = s.getEnabledProtocols();
      cipher = s.getEnabledCipherSuites();
      Logger.info(socketType + " Socket receive buffer size: " + s.getReceiveBufferSize());
    }
    StringBuffer sb = new StringBuffer("enabled protocols for " + socketType + " socket: ");
    for (int i=0;i<protos.length;++i) 
    {
      sb.append(protos[i]);
      sb.append(" ");
    } 
    Logger.debug(sb.toString());

    sb = new StringBuffer("enabled cipher suites for " + socketType + " socket: ");
    for (int i=0;i<cipher.length;++i) 
    {
      sb.append(cipher[i]);
      sb.append(" ");
    } 
    Logger.debug(sb.toString());
  }
}

/*********************************************************************
 * $Log: SSLRMISocketFactory.java,v $
 * Revision 1.7  2005/01/12 11:32:43  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2005/01/12 01:44:57  willuhn
 * @N added test https server
 *
 * Revision 1.5  2005/01/12 00:17:17  willuhn
 * @N JameicaTrustManager
 *
 * Revision 1.4  2005/01/11 00:52:52  willuhn
 * @RMI over SSL works
 *
 * Revision 1.3  2005/01/11 00:00:52  willuhn
 * @N SSLFactory
 *
 * Revision 1.2  2004/09/13 23:27:12  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2004/08/31 18:57:23  willuhn
 * *** empty log message ***
 *
 **********************************************************************/
