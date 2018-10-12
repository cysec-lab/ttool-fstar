/*
 * Copyright or (C) or Copr. GET / ENST, Telecom-Paris, Arthur VUAGNIAUX
 */

package gui.test.main;

import ui.MainGUI;

/*
 * Class Main
 * Creation: 09/10/2018
 * @version 1.0 09/10/2018
 * @author Arthur VUAGNIAUX
*/

public class Main extends MainGUI {

	public Main(boolean _openLast, boolean _turtleOn, boolean _systemcOn, boolean _lotosOn, boolean _proactiveOn,
			boolean _tpnOn, boolean _osOn, boolean _uppaalOn, boolean _ncOn, boolean _avatarOn, boolean _proverifOn,
			boolean _avatarOnly, boolean _experimental) {
		super(_openLast, _turtleOn, _systemcOn, _lotosOn, _proactiveOn, _tpnOn, _osOn, _uppaalOn, _ncOn, _avatarOn, _proverifOn,
				_avatarOnly, _experimental);
		super.build();
	}
}
