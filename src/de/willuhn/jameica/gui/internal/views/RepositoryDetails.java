/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.gui.internal.views;

import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.internal.action.Settings;
import de.willuhn.jameica.gui.internal.controller.RepositoryContol;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.I18N;

/**
 * Detail-Ansicht eines Repositories.
 */
public class RepositoryDetails extends AbstractView
{
  private final static I18N i18n = Application.getI18n();

  /**
   * @see de.willuhn.jameica.gui.AbstractView#bind()
   */
  public void bind() throws Exception
  {
    RepositoryContol control = new RepositoryContol(this);

    GUI.getView().setTitle(i18n.tr("Repository: {0}",control.getRepository().getName()));
    
    SimpleContainer group = new SimpleContainer(getParent(),true);
    group.addInput(control.getRepositories());
    group.addPart(control.getPlugins());
    
    ButtonArea buttons = new ButtonArea();
    buttons.addButton(i18n.tr("Plugin-Repositories verwalten"),new Settings(),i18n.tr("Updates"),false,"document-properties.png");
    group.addButtonArea(buttons);
  }

  /**
   * @see de.willuhn.jameica.gui.AbstractView#canBookmark()
   */
  public boolean canBookmark()
  {
    return false;
  }

}
