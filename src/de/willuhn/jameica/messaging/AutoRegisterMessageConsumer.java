/**********************************************************************
 * $Source: /cvsroot/jameica/jameica/src/de/willuhn/jameica/messaging/AutoRegisterMessageConsumer.java,v $
 * $Revision: 1.1 $
 * $Date: 2011/06/07 11:08:55 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.messaging;

import java.lang.reflect.Constructor;

import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;

/**
 * Uebernimmt das Suchen und Aktivieren der automatisch zu registrierenden Message-Consumer.
 * Das Aktivieren der automatisch zu registrierenden Message-Consumer machen wir deshalb hier,
 * damit die erst dann gesucht werden, wenn das System komplett gebootet ist. Vorher
 * wurde das direkt in MessagingFactory#sendMessage gemacht. Mit dem Effekt, dass
 * die Consumer eventuell viel zu frueh gesucht wurden - noch bevor die Klassen
 * geladen wurden. Beispielsweise schickt der Deploy-Service Messages, wenn er
 * Plugins installiert oder aktualisiert hat. Zu dem Zeitpunkt darf aber noch nicht
 * nach den Consumern gesucht werden - da wuerden noch keine gefunden werden.
 * 
 * Daher machen wir das jetzt erst dann, wenn das System komplett gebootet wurde.
 * Nachrichten koennen zwar auch schon vorher geschickt werden. Aber die automatisch
 * registrierten stehen erst zur Verfuegung, nachdem alles gestartet ist.
 */
public class AutoRegisterMessageConsumer implements MessageConsumer
{
  private static boolean done = false;
  
  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#getExpectedMessageTypes()
   */
  public Class[] getExpectedMessageTypes()
  {
    return new Class[]{SystemMessage.class};
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#handleMessage(de.willuhn.jameica.messaging.Message)
   */
  public void handleMessage(Message message) throws Exception
  {
    if (done)
      return;
    
    // Machen wir nur, wenn die "System-gestartet"-Meldung kommt.
    SystemMessage msg = (SystemMessage) message;
    if (msg.getStatusCode() != SystemMessage.SYSTEM_STARTED)
      return;
    
    Logger.info("searching for auto-registered message consumers");
    MessagingFactory factory = Application.getMessagingFactory();
    Class<MessageConsumer>[] c = new Class[0];
    try
    {
      c = Application.getClassLoader().getClassFinder().findImplementors(MessageConsumer.class);
    }
    catch (ClassNotFoundException e)
    {
      Logger.info("  no messaging consumers found");
    }
    for (int i=0;i<c.length;++i)
    {
      if (c[i].getName().indexOf('$') != -1)
      {
        Logger.debug(c[i].getName() + " is an inner class, skipping");
        continue;
      }
      try
      {
        Constructor ct = c[i].getConstructor((Class[])null);
        ct.setAccessible(true);
        MessageConsumer mc = (MessageConsumer)(ct.newInstance((Object[])null));
        if (mc.autoRegister())
        {
          Logger.info("  " + c[i].getName());
          factory.registerMessageConsumer(mc);
        }
      }
      catch (NoSuchMethodException e)
      {
        Logger.warn("message consumer has no default constructor, skipping");
      }
      catch (Throwable t)
      {
        Logger.error("unable to register message consumer " + c[i].getName(),t);
      }
    }
  }

  /**
   * @see de.willuhn.jameica.messaging.MessageConsumer#autoRegister()
   */
  public boolean autoRegister()
  {
    return false;
  }

}



/**********************************************************************
 * $Log: AutoRegisterMessageConsumer.java,v $
 * Revision 1.1  2011/06/07 11:08:55  willuhn
 * @C Nach automatisch zu registrierenden Message-Consumern erst suchen, nachdem die SystemMessage.SYSTEM_STARTED geschickt wurde. Vorher geschah das bereits beim Senden der ersten Nachricht - was u.U. viel zu frueh ist (z.Bsp. im DeployService)
 *
 **********************************************************************/