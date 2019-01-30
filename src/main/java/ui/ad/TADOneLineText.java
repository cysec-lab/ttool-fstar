package ui.ad;

import ui.ScalableTGComponent;
import ui.TDiagramPanel;
import ui.TGCOneLineText;
import ui.TGComponent;
import ui.ZoomUtil;

public abstract class TADOneLineText extends TGCOneLineText implements ScalableTGComponent {

	public TADOneLineText(int _x, int _y, int _minX, int _maxX, int _minY, int _maxY, boolean _pos, TGComponent _father,
			TDiagramPanel _tdp) {
		super(_x, _y, _minX, _maxX, _minY, _maxY, _pos, _father, _tdp);
	}
    
    /**
     * Issue #69
     * @return	:	True if OneLineText is able to disable
     */
    @Override
    public boolean canBeDisabled() {
    	final CanBeDisabledADElementVisitor visitor = new CanBeDisabledADElementVisitor();
    	acceptBackward( visitor );
    	
    	return visitor.isCanBeDisabled();
    }
    
    @Override
    public void rescale( final double scaleFactor ) {
    	ZoomUtil.rescale( this, scaleFactor );
    }
}
