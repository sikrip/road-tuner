/*
 *  Copyright (c) 2005-2012, EFI Analytics. All Rights Reserved.
 *
 *  For questions or additional information contact:
 *  Phil Tobin
 *  EFI Analytics
 *  p_tobin@yahoo.com
 *
 */

package exampletunerstudioplugin;

import com.efiAnalytics.plugin.ecu.OutputChannel;
import com.efiAnalytics.plugin.ecu.OutputChannelClient;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Philip Tobin
 */
public class OutputChannelLabel extends JPanel implements OutputChannelClient{
    OutputChannel outputChannel = null;
    JLabel lblValue = null;
    DecimalFormat df = null;

    public OutputChannelLabel(OutputChannel outputChannel){
        this.outputChannel = outputChannel;
        setLayout(new BorderLayout(4,4));
        String title = outputChannel.getName();
        if(outputChannel.getUnits()!=null && !outputChannel.getUnits().isEmpty()){
            title += "("+outputChannel.getUnits()+") :";
        }else{
            title += " :";
        }
        JLabel lblTitle = new JLabel(title, JLabel.RIGHT);
        add(BorderLayout.CENTER, lblTitle);
        lblValue = new JLabel("#####", JLabel.LEFT);
        lblValue.setMinimumSize(new Dimension(45, 18));
        lblValue.setPreferredSize(new Dimension(45, 18));
        add(BorderLayout.EAST, lblValue);
    }

    public void setCurrentOutputChannelValue(String channelName, double value) {
        lblValue.setText(getFormatter().format(value));
    }

    private DecimalFormat getFormatter(){
        if(df == null){
            df = (DecimalFormat) DecimalFormat.getInstance();
            df.setMaximumFractionDigits(4);
        }
        return df;
    }

}
