/* Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Ludovic Apvrille
 * 
 * ludovic.apvrille AT enst.fr
 * 
 * This software is a computer program whose purpose is to allow the
 * edition of TURTLE analysis, design and deployment diagrams, to
 * allow the generation of RT-LOTOS or Java code from this diagram,
 * and at last to allow the analysis of formal validation traces
 * obtained from external tools, e.g. RTL from LAAS-CNRS and CADP
 * from INRIA Rhone-Alpes.
 * 
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 * 
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 * 
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 * 
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */

package ui.avatarmethodology;

import myutil.GraphicLib;
import ui.*;
import ui.util.IconManager;

import java.awt.*;
import java.util.Vector;

//import java.awt.geom.*;

/**
 * Class AvatarMethodologyConnector Connector to be used in Avatar methodology.
 * Connects two diagram references Creation: 26/08/2014
 * 
 * @version 1.0 26/08/2014
 * @author Ludovic APVRILLE
 */
public class AvatarMethodologyConnector extends TGConnectorWithCommentConnectionPoints {
  int w, h;

  public AvatarMethodologyConnector(int _x, int _y, int _minX, int _minY, int _maxX, int _maxY, boolean _pos,
      TGComponent _father, TDiagramPanel _tdp, TGConnectingPoint _p1, TGConnectingPoint _p2, Vector<Point> _listPoint) {
    super(_x, _y, _minX, _minY, _maxX, _maxY, _pos, _father, _tdp, _p1, _p2, _listPoint);
    value = "<<deriveReqt>>";

    myImageIcon = IconManager.imgic1008;

    removable = false;
  }

  /**
   * drawLastSegment: calling arrowWithLine Is Linking 2 diagram references ?
   * (x1,y1) and (x2, y2)
   * 
   * @param g  grahics
   * @param x1 int
   * @param y1 int
   * @param x2 int
   * @param y2 int
   */
  @Override
  protected void drawLastSegment(Graphics g, int x1, int y1, int x2, int y2) {

    // g.drawLine(x1, y1, x2, y2);
    GraphicLib.arrowWithLine(g, 1, 1, 0, x1, y1, x2, y2, false);

  }

  /**
   * extraIsOnOnlyMe
   * 
   * @param x1 int
   * @param y1 int
   **/
  @Override
  public TGComponent extraIsOnOnlyMe(int x1, int y1) {
    if (GraphicLib.isInRectangle(x1, y1, (p1.getX() + p2.getX() - w) / 2, (p1.getY() + p2.getY()) / 2 - h, w, h)) {
      return this;
    }
    return null;
  }

  /**
   * get the type of the current avatar methodology connector ?
   * 
   * @return int denoting of the type avatar methodology connector
   */
  @Override
  public int getType() {
    return TGComponentManager.AVATARMETHODOLOGY_CONNECTOR;
  }

}
