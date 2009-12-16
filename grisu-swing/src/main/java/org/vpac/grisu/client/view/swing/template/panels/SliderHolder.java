package org.vpac.grisu.client.view.swing.template.panels;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class SliderHolder extends JPanel implements ComponentHolder {

	private JTextField textPane;
	private JSlider slider_1;
	static final Logger myLogger = Logger.getLogger(SliderHolder.class
			.getName());

	private int min;
	private int max;
	private int defaultValue;
	private int majorTicks;
	private int minorTicks;

	private JSlider slider = null;
	private BoundedRangeModel sliderModel = null;

	private AbstractInputPanel parentPanel = null;

	public SliderHolder(AbstractInputPanel parent, int min, int max,
			int defaultValue, int majorTicks, int minorTicks) {
		this.parentPanel = parent;
		this.min = min;
		this.max = max;
		this.majorTicks = majorTicks;
		this.minorTicks = minorTicks;
		this.defaultValue = defaultValue;
		sliderModel = new DefaultBoundedRangeModel(defaultValue, 0, min, max);
		setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("default:grow(1.0)"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("28dlu") },
				new RowSpec[] { FormFactory.RELATED_GAP_ROWSPEC,
						RowSpec.decode("40dlu"),
						FormFactory.RELATED_GAP_ROWSPEC }));
		add(getSlider(), new CellConstraints(1, 2, CellConstraints.FILL,
				CellConstraints.FILL));
		add(getTextPane(), new CellConstraints(3, 2, CellConstraints.FILL,
				CellConstraints.FILL));
	}

	public Component getComponent() {

		return this;
	}

	public String getExternalSetValue() {
		return Integer.toString(sliderModel.getValue());
	}

	public int getRowSpec() {
		return 45;
	}

	/**
	 * @return
	 */
	protected JSlider getSlider() {
		if (slider_1 == null) {
			slider_1 = new JSlider(sliderModel);
			// // seems to be a bug in jslider
			// int tmp = sliderModel.getValue();
			// slider_1.setValue(sliderModel.getMinimum());
			// slider_1.setValue(tmp);
			// slider_1.revalidate();
			// slider_1.setUI(new BasicSliderUI(slider_1));
			slider_1.setSnapToTicks(true);
			slider_1.setPaintLabels(true);
			slider_1.setMajorTickSpacing(majorTicks);
			slider_1.setMinorTickSpacing(minorTicks);
			slider_1.setPaintTicks(true);
			slider_1.addChangeListener(new ChangeListener() {
				public void stateChanged(final ChangeEvent e) {
					getTextPane().setText(
							Integer.toString(sliderModel.getValue()));
				}
			});
		}
		return slider_1;
	}

	/**
	 * @return
	 */
	protected JTextField getTextPane() {
		if (textPane == null) {
			textPane = new JTextField();
			textPane.setHorizontalAlignment(SwingConstants.CENTER);
			textPane.setBackground(Color.WHITE);
			textPane.setEditable(false);
			textPane.setText(Integer.toString(defaultValue));
		}
		return textPane;
	}

	public void setComponentField(String value) {

		try {
			int temp = Integer.parseInt(value);
			sliderModel.setValue(temp);
		} catch (Exception e) {
			// do nothing
			myLogger.warn("Can't convert value " + value + " to integer.");
			return;
		}

	}

}
